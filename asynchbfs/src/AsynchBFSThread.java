/**
 * Synch GHS Algorithm
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
	private int requestedTerminationCount;
	private int terminatedCount;
    private Map<Link, Queue<Message>> outboundMessages;
    private List<Predicate<AsynchBFSThread>> pendingFunctions;
    private Courier courier;

	public AsynchBFSThread(String id, Phaser phaser) {
		this.phaser = phaser;
        node = new Node(id);
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
        outboundMessages = new HashMap<>();
        courier = new Courier();
	}

    /*
        Broadcast a message to all adjacent nodes
     */
    public void broadcastMessage(Message msg) {
        for (Node neighbor : node.neighbors) {
            neighbor.inboundMessages.get(node).add(msg);
        }
    }

    /*
        Send a distance update to all neighbors except for the parent
     */
	public void broadcastDistance() {

	}

    /*
        Create and add an ack lambda function for the current distance and parent
     */
    public void createAckLambda() {

    }


    /*
        processMessages is always executed before the end of every round.
     */
	public void processMessages() {
		int roundTerminatedCount = 0;
		while (roundTerminatedCount < node.neighbors.size() - terminatedCount) {
			for (Node neighbor : node.neighbors) {
                List<Message> msgs = node.inboundMessages.get(neighbor);
				synchronized (msgs) {
					while (!msgs.isEmpty()) {
						Message msg = msgs.remove(0);
						switch (msg.type) {
						case RoundTermination:
							roundTerminatedCount++;
							break;
						case AlgoTermination:
							terminatedCount++;
                            break;
                        case DistanceUpdate:
                            processDistanceUpdate(msg, neighbor);
						}
					}
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
            if(distance.intValue() + 1 < node.distance) {
                node.distance = distance.intValue() + 1;
                node.parent = src;
            }
            return true;
        };
        pendingFunctions.add(distUpdate);
    }

	public void end() {
        broadcastMessage(new Message(Message.MessageType.AlgoTermination));
        if(DEBUG)
            print(String.format("%s\n", node));
		phaser.arriveAndDeregister();
	}

	public void print(String msg) {
		System.out.format("ID %s (round %d): %s\n", node.ID, round, msg);
	}

	public void run() {
        while(node.parent == null || pendingFunctions.size() > 0) {
            waitForRound();
        }
        end();
    }


	public void waitForRound() {
		try {
            broadcastMessage(new Message(Message.MessageType.RoundTermination));
            processMessages();
			phaser.arriveAndAwaitAdvance();
			round++;
            // send messages from last message process
            for(Link link: outboundMessages.keySet()) {
                Queue<Message> messages = outboundMessages.get(link);
                while(!messages.isEmpty()) {
                    link.sendMessage(messages.poll());
                }
            }
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
