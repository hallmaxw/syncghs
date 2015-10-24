/**
 * Created by maxwell on 10/10/15.
 */
import java.nio.file.LinkOption;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.SynchronousQueue;

public class SyncGHSThread extends Thread {
	private String id;
	private String componentId;
    private String leaderID;
	private Phaser phaser;
    private Node node;
	private State state;
	private int round;
	private int requestedTerminationCount;
	private int terminatedCount;
	private int level;
	private Queue<Message> inboundMessage;
	private Link testLink;
	private Map<String, List<Message>> idMessageInboundMap;
	private double bestWeight;
	private int findCount;

	public SyncGHSThread(String id, Phaser phaser) {
		this.id = id;
		this.componentId = id;
		this.phaser = phaser;
		this.leaderID = id;
		this.state = State.Initialization;
        node = new Node(id);
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
		this.level = 0;
		inboundMessage = new SynchronousQueue<Message>();
		idMessageInboundMap = new HashMap<String, List<Message>>();
		testLink = new Link();
	}

	public void broadcastMessage(Message msg) {
		for (Link link : node.allLinks) {
			link.sendMessage(msg);
		}
	}

//	public void processMessages() {
//		int roundTerminatedCount = 0;
//		while (roundTerminatedCount < links.size() - terminatedCount) {
//			for (Link link : links) {
//				synchronized (link.inboundMessages) {
//					while (!link.inboundMessages.isEmpty()) {
//						Message msg = link.inboundMessages.remove(0);
//						switch (msg.type) {
//						case TextMessage:
//							print(String.format("Received message:(%s) %s", msg.type.name(), msg.data));
//							break;
//						case RoundTermination:
//							roundTerminatedCount++;
//							break;
//						case AlgoTerminationRequest:
//							requestedTerminationCount++;
//							break;
//						case AlgoTermination:
//							terminatedCount++;
//						}
//					}
//				}
//			}
//		}
//		print("Processed messages");
//	}

	public void end() {
		broadcastMessage(new Message(Message.MessageType.AlgoTermination));
		print("Finished");
		phaser.arriveAndDeregister();
	}

	public void print(String msg) {
		System.out.format("ID %s (round %d): %s\n", id, round, msg);
	}

	/*
	 * Get the minimum weight associated with the node
	 */
	public Link findMinEdge() {
        print(String.format("MIN: %s", node.potentialLinks.get(0).toString()));
        return node.potentialLinks.get(0);
	}

    public Link findMWOE() {
        // Find local candidate
        Link localCand = findMinEdge();
        if(node.parent == null) { // local leader

        }
        return null;
    }


	/*
	 * (non-Javadoc) read messages and then process
	 */

	public void readMessage() {
		for (Link link : node.allLinks) {
            List<Message> inboundMsgs = idMessageInboundMap.get(link.destinationId);
			synchronized (inboundMsgs) {
                while(inboundMsgs.size() > 0) {
                    Message message = inboundMsgs.remove(0);
                    switch (message.type) {
                        case Connect:
                            connect(message.level, link);
                            break;
                        case Initiate :
                            initiate(message.level, id, state);
                            break;
                        case TestMessage :
                            testMessage(message.level, id, link);
                            break;
                        case Accept :
                            accept(link);
                            break;
                        case Reject :
                            reject(link);
                        case ReportMessage :
                            reportMessage(link.weight, link);
                            break;
                        case ChangeRootMessage :
                            changeRootMessage();
                            break;

                    }
                }
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * Attempts to merge two trees
	 * @see java.lang.Thread#run()
	 */

	public void connect(int L, Link link) {
		// TODO: add this feature
		// if this node is sleeping then make a wake up call

		if (L < this.level) {
			// change to Branch State
			link.state = Link.State.Branch;

		} else if (link.state == Link.State.Basic) { // connect
			Message message = new Message();
			message.type = Message.MessageType.Connect;
			message.level = L;
			message.weight = link.weight;
			this.inboundMessage.add(message);
		}

		else { // Initiate
			Message message = new Message();
			message.type = Message.MessageType.Initiate;
			message.level = L + 1;
			message.state = Message.State.Find;
			message.weight = link.weight;
			this.inboundMessage.add(message);

		}
	}

	/*
	 * 
	 * Intiate function
	 */
	public void initiate(int L, String id, State state) {
		this.level = L;
		this.id = id;
		this.state = State.Find;
		for (Link link : node.allLinks) {
			if (link.state == Link.State.Branch) { // 1 is for Branch
				Message message = new Message();
				message.type = Message.MessageType.Initiate;
				message.level = L;
				message.id = id;
				message.weight = link.weight;
				idMessageInboundMap.get(link.destinationId).add(message);
			}
		}

		if (state == State.Find) {
			// call test function
		}

	}

	public void test() {
		double min = Double.MAX_VALUE;
		Link minLink = null;
		for (Link link : node.allLinks) {
			if (link.state == Link.State.Basic) {
				if (min > link.weight) {
					minLink = link;
					min = link.weight;
				}
			}
		}

		if (min != Double.MAX_VALUE) {
			this.testLink = minLink;
			Message message = new Message();
			message.type = Message.MessageType.TestMessage;
			message.level = this.level;
			message.id = this.id;
			message.weight = minLink.weight;
			idMessageInboundMap.get(minLink.destinationId).add(message);
		} else {
			this.testLink = null;
			// call function report
		}

	}

	public void testMessage(int L, String id, Link link) {
		if (L > this.level) {
			Message message = new Message();
			message.type = Message.MessageType.TestMessage;
			message.level = L;
			message.id = id;
			message.weight = link.weight;
			idMessageInboundMap.get(this.id).add(message);
		} else if (id == this.id) {
			if (link.state == Link.State.Basic)
				link.state = Link.State.Rejected;

			if (link != this.testLink) {
				Message message = new Message();
				message.type = Message.MessageType.Reject; // Reject
				message.weight = link.weight;
				idMessageInboundMap.get(link.destinationId).add(message);
			} else {
				test();
			}
		} else {
			Message message = new Message();
			message.type = Message.MessageType.Accept;
			message.weight = link.weight;
			idMessageInboundMap.get(link.destinationId).add(message);
		}
	}

	/*
	 * 
	 */
	public void accept(Link link) {
		this.testLink = null;
		if (link.weight < this.bestWeight) {
			this.bestWeight = link.weight;
		}

		// call report fucntion
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */

	public void reject(Link link) {
		if (link.state == Link.State.Basic) {
			link.state = Link.State.Rejected;

		}

		test();
	}
	
	public void report() {
//		int k =0 ;
//		for (Link link : links) {
//			 if(link.state == Link.State.Branch && link.destinationId != this.parentId)
//				 k++;
//		}
//
//		if(findCount == k && this.testLink == null) {
//			this.state = null; // 2 have to assigneed here
//			Message message = new Message();
//			message.type = Message.MessageType.ReportMessage;
//			// message.weight = this.bestWeight;
//			// message.weight = 				 // we need srouce destination mapping in link
//			// idMessageInboundMap.get(key)      // same as above
//		}
//
	}
	
	/*
	 * 
	 * 
	 */
	public void reportMessage(double weight, Link link) {
//		if(link.destinationId != this.parentId) {
//			if(weight < this.bestWeight) {
//				this.bestLink = link;
//				this.bestWeight = weight;
//			}
//
//			// increment find count by 1
//			// call report function
//		}
//		else {
//			if(this.state == State.Find) {
//				Message message  = new Message();
//				message.type =  null ; // have to add 5 here what is it ?
//				message.weight =weight;
//				idMessageInboundMap.get(this.id).add(message);
//			} else if(weight > this.bestWeight) {
//				// call change root function
//			} else {
//				// print output  // stop the program
//			}
//		}
	}

	public void changeRoot() {
		Message message = new Message();
		
	}


	
	public void changeRootMessage() {
		changeRoot();
	}

	public void run() {
        Collections.sort(node.potentialLinks);
        Link mwoe = findMWOE();
		readMessage();
		
	}

	public void waitForRound() {
		try {
			phaser.arriveAndAwaitAdvance();
			round++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addLink(Link link) {
		node.addLink(link);
        idMessageInboundMap.put(link.destinationId, link.inboundMessages);
	}

	enum State {
		Connect, Initialization, SendMessages, ProcessMessages, End, Find , Found 
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("THREAD %s\n", id));
		for (Link link : node.allLinks) {
			builder.append(String.format("%s -- %s --> %s\n", id, link.weight, link.destinationId));
		}
		return builder.toString();
	}
}
