/**
 * Asynch BFS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */

import java.util.*;
import java.util.concurrent.Phaser;
import java.util.function.Predicate;

public class AsynchBFSThread extends Thread {
    static public final boolean DEBUG = false;
	private Phaser phaser;
    private Node node;
	private int round;
	private int terminatedCount;
    private List<Predicate<AsynchBFSThread>> pendingFunctions;
    private Courier courier;
    private Map<Node, Boolean> acksReceived;

	public AsynchBFSThread(Node node, Phaser phaser) {
		this.phaser = phaser;
        this.node = node;
		round = 1;
		terminatedCount = 0;
        pendingFunctions = new LinkedList<>();
        courier = new Courier();
        acksReceived = new HashMap<>();

        // If the node has a parent, this is the root. We need to do the following:
        // 1) Create a lambda to broadcast the first dist update.
        // 2) Create an ack lambda
        if(node.parent != null) {
            node.distance = 0;
            createBroadcastDistanceLambda();
            createAckLambda();
        }
	}

    /*
        Broadcast a message to all adjacent nodes
     */
    public void broadcastMessage(Message msg) {
        for (Node neighbor : node.neighbors) {
            neighbor.inboundMessages.add(msg);
        }
    }

    /*
        Send a distance update to all neighbors except for the parent
     */
	public void broadcastDistance() {
        for(Node neighbor: node.neighbors) {
            if(neighbor == node.parent)
                continue;
            Message msg = new Message(node, Message.MessageType.DistanceUpdate, Integer.valueOf(node.distance));
            msg.round = round;
            courier.addMessage(msg, neighbor);
        }
	}

    public void createBroadcastDistanceLambda() {
        Predicate<AsynchBFSThread> broadcast = (AsynchBFSThread t) -> {

            if(round > 200) {
                print("Running broadcast lambda");
            }
            broadcastDistance();
            return true;
        };
        pendingFunctions.add(broadcast);
    }

    /*
        Create and add an ack lambda function for the current distance and parent
     */
    public void createAckLambda() {
        acksReceived.clear();
        final int distance = node.distance;
        Predicate<AsynchBFSThread> lambda = (AsynchBFSThread t) -> {

            // the distance was updated
            if(distance != node.distance){
                return true;
            }


            int expectedAcks = node.neighbors.size()-1;
            if(node.parent == node) {
                expectedAcks += 1;
            }

            if(round > 200) {
                print(String.format("ACK LAMBDA: HAVE %d NEED %d\n", acksReceived, expectedAcks));
            }
            // every neighbor except the parent has sent an ack
            if(acksReceived.size() == expectedAcks) {
                Message msg = new Message(node, Message.MessageType.Ack, Integer.valueOf(node.distance-1));
                courier.addMessage(msg, node.parent);
                return true;
            } else {
                return false;
            }

        };
        pendingFunctions.add(lambda);
    }


    /*
        processMessages is always executed before the end of every round.
     */
	public void processMessages() {
		
		int roundTerminatedCount = 0;
		
		while(roundTerminatedCount < node.neighbors.size() - terminatedCount) {
			if(!node.inboundMessages.isEmpty()) {
				Message message =  node.inboundMessages.remove(0);
				switch(message.type) {
				case RoundTermination:
                    //print("RoundTermination msg");
				    roundTerminatedCount++;
					break;
				case AlgoTermination:
					terminatedCount++;
                    // remove from neighbors
                    acksReceived.put(message.source, true);
                    break;
                case DistanceUpdate:
                    print(String.format("DistanceUpdate msg from %s", message.source.ID));
                	processDistanceUpdate(message, message.source);
                    break;
                case Ack:
                    print("Ack msg");
                    processAck(message, message.source);
                    break;
				default:
                    System.err.println("Message handler not implemented");
                    System.err.printf("Message Type: %s", message.type.name());
                    System.exit(1);
					break;
				}
			}
			
		}
		
		
	}

    /*
        processDistanceUpdate adds a predicate that will update the parent and distance if
        the given distance is better than the current distance
     */
    public void processDistanceUpdate(Message msg, Node src) {
        Integer distance = (Integer) msg.data;
        Predicate<AsynchBFSThread> distUpdate = (AsynchBFSThread t) -> {
            if(round > 200) {
                print("Running dist update");
            }
            if(distance.intValue() + 1 < node.distance) {
                node.distance = distance.intValue() + 1;
                node.parent = src;
            } else {
                Message ackMsg = new Message(node, Message.MessageType.Ack, distance);
                courier.addMessage(ackMsg, src);
            }
            return true;
        };
        pendingFunctions.add(distUpdate);
    }

    public void processAck(Message msg, Node src) {
        Integer distance = (Integer) msg.data;
        if(distance.intValue() == node.distance) {
            acksReceived.put(src, true);
            print(String.format("Received ACK from %s", src.ID));
        }
    }

	public void end() {
        broadcastMessage(new Message(node, Message.MessageType.AlgoTermination));
        print(String.format("%s\n", node));
		phaser.arriveAndDeregister();
	}

	public void print(String msg) {
        if(DEBUG)
		    System.out.format("ID %s (round %d): %s\n", node.ID, round, msg);
	}

	public void run() {
        while(node.parent == null || pendingFunctions.size() > 0) {
            Node prevParent = node.parent;
            int prevDistance = node.distance;

            courier.sendMessages(round);
            executePendingFunctions();
            boolean updated = false;
            if(prevParent != node.parent) {
                updated = true;
            } else {
                if(prevDistance != node.distance) {
                    updated = true;
                }
            }
            if(updated) {
                print(String.format("Distance updated to %d", node.distance));
                createBroadcastDistanceLambda();
                createAckLambda();
            }
            waitForRound();
        }
        end();
    }


    public void executePendingFunctions() {
        Iterator<Predicate<AsynchBFSThread>> iter = pendingFunctions.iterator();
        while(iter.hasNext()) {
            Predicate<AsynchBFSThread> lambda = iter.next();
            boolean result = lambda.test(this);
            if(result)  {
                iter.remove();
            }
        }
    }

	public void waitForRound() {
		try {
            broadcastMessage(new Message(node, Message.MessageType.RoundTermination));
            processMessages();
            if(node.ID.equals("7"))
                print(String.format("Round %d ended", round));
            if(round > 200)
                print("Checking in");
			phaser.arriveAndAwaitAdvance();
			round++;
            // send messages from last message process
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("THREAD %s\n", node.ID));
		return builder.toString();
	}
}
