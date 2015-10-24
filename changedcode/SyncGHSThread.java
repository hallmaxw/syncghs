/**
 * Created by maxwell on 10/10/15.
 */
import java.nio.file.LinkOption;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.SynchronousQueue;

public class SyncGHSThread extends Thread {
    private String leaderID; // not sure if we need this
	private Phaser phaser;
    private Node node;
	private State state;
	private int round;
	private int requestedTerminationCount;
	private int terminatedCount;
	private int level;
    private Map<Link, Link> mwoeResponses; // map to store our children's responses to MWOE search
    private Map<Link, Message> testResponses;
    private Map<Link, Queue<Message>> outboundMessages;

	public SyncGHSThread(String id, Phaser phaser) {
		this.phaser = phaser;
		this.leaderID = id;
		this.state = State.Initialization;
        node = new Node(id, id);
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
		this.level = 0;
        mwoeResponses = new HashMap<Link, Link>();
        outboundMessages = new HashMap<Link, Queue<Message>>();
        testResponses = new HashMap<Link, Message>();
	}

	public void broadcastMessage(Message msg) {
		for (Link link : node.allLinks) {
			link.sendMessage(msg);
		}
	}

	public void processMessages() {
		int roundTerminatedCount = 0;
		while (roundTerminatedCount < node.allLinks.size() - terminatedCount) {
			for (Link link : node.allLinks) {
				synchronized (link.inboundMessages) {
					while (!link.inboundMessages.isEmpty()) {
						Message msg = link.inboundMessages.remove(0);
						switch (msg.type) {
						case TextMessage:
							print(String.format("Received message:(%s) %s", msg.type.name(), msg.data));
							break;
                        case TestRequest:
                            processTestRequest(link, msg);
                            break;
                        case TestResponse:
                            processTestResponse(link, msg);
						case RoundTermination:
							roundTerminatedCount++;
							break;
						case AlgoTerminationRequest:
							requestedTerminationCount++;
							break;
						case AlgoTermination:
							terminatedCount++;
						}
					}
				}
			}
		}
		print("Processed messages");
	}

    public void processTestRequest(Link link, Message msg) {
        outboundMessages.get(link).add(new Message(Message.MessageType.TestResponse, true));
    }

    public void processTestResponse(Link link, Message msg) {
        testResponses.put(link, msg);
    }

	public void end() {
		broadcastMessage(new Message(Message.MessageType.AlgoTermination));
		print("Finished");
		phaser.arriveAndDeregister();
	}

	public void print(String msg) {
		System.out.format("ID %s (round %d): %s\n", node.ID, round, msg);
	}

	/*
	 * Get the minimum weight associated with the node
	 */
	public Link findLocalMinEdge() {
        for(Link candidate : node.potentialLinks) {
            boolean isOutgoing = testLink(candidate);
            if(isOutgoing) {
                return candidate;
            } else {
                node.potentialLinks.remove(candidate);
                node.rejectedLinks.add(candidate);
            }
        }
        return null;
	}

    /*
        tests if the provided link is outgoing or not

        1) Send test message
        2) Wait for response
     */
    public boolean testLink(Link link) {
        Message testMsg = new Message(Message.MessageType.TestRequest, node.componentId);
        testMsg.level = level;
        link.sendMessage(testMsg);
        waitForRound();

        Message response = null;
        while(response == null) {
            if(testResponses.containsKey(link)){
                response = testResponses.remove(link);
            }
            if(response == null) {
                waitForRound();
            }
        }
        return (Boolean) response.data;
    }

    public Link findMWOE() {
        mwoeResponses.clear();
        // tell children to find mwoe
        for(Link link: node.children) {
            link.sendMessage(new Message(Message.MessageType.MWOEInit));
        }
        // Find local candidate
        Link localCandidate = findLocalMinEdge();
        // wait for children to respond
        while(mwoeResponses.size() != node.children.size()) {
            waitForRound();
        }
        Link bestCandidate = null;
        if(node.children.size() > 0) {
            Link childCandidate = Collections.min(mwoeResponses.values());
            bestCandidate = childCandidate.compareTo(localCandidate) < 0 ? childCandidate : localCandidate;
        } else {
            bestCandidate = localCandidate;
        }
        if(node.parent == null) { // local leader
            print(String.format("%s\n", bestCandidate));
        }
        return bestCandidate;
    }


	/*
	 * (non-Javadoc) read messages and then process
	 */

	public void readMessage() {
//		for (Link link : node.allLinks) {
//            List<Message> inboundMsgs = idMessageInboundMap.get(link.destinationId);
//			synchronized (inboundMsgs) {
//                while(inboundMsgs.size() > 0) {
//                    Message message = inboundMsgs.remove(0);
//                    switch (message.type) {
//                        case Connect:
//                            connect(message.level, link);
//                            break;
//                        case TestMessage :
//                            testMessage(message.level, node.ID, link);
//                            break;
//                        case Accept :
//                            accept(link);
//                            break;
//                        case Reject :
//                            reject(link);
//                        case ReportMessage :
//                            reportMessage(link.weight, link);
//                            break;
//                        case ChangeRootMessage :
//                            changeRootMessage();
//                            break;
//
//                    }
//                }
//			}
//		}
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
		}

		else { // Initiate
			Message message = new Message();
			message.type = Message.MessageType.Initiate;
			message.level = L + 1;
			message.state = Message.State.Find;
			message.weight = link.weight;

		}
	}


	public void test() {
//		double min = Double.MAX_VALUE;
//		Link minLink = null;
//		for (Link link : node.allLinks) {
//			if (link.state == Link.State.Basic) {
//				if (min > link.weight) {
//					minLink = link;
//					min = link.weight;
//				}
//			}
//		}
//
//		if (min != Double.MAX_VALUE) {
//			this.testLink = minLink;
//			Message message = new Message();
//			message.type = Message.MessageType.TestMessage;
//			message.level = this.level;
//			message.id = this.node.ID;
//			message.weight = minLink.weight;
//			idMessageInboundMap.get(minLink.destinationId).add(message);
//		} else {
//			this.testLink = null;
//			// call function report
//		}

	}

	public void testMessage(int L, String id, Link link) {
//		if (L > this.level) {
//			Message message = new Message();
//			message.type = Message.MessageType.TestMessage;
//			message.level = L;
//			message.id = id;
//			message.weight = link.weight;
//			idMessageInboundMap.get(this.node.ID).add(message);
//		} else if (id == this.node.ID) {
//			if (link.state == Link.State.Basic)
//				link.state = Link.State.Rejected;
//
//			if (link != this.testLink) {
//				Message message = new Message();
//				message.type = Message.MessageType.Reject; // Reject
//				message.weight = link.weight;
//				idMessageInboundMap.get(link.destinationId).add(message);
//			} else {
//				test();
//			}
//		} else {
//			Message message = new Message();
//			message.type = Message.MessageType.Accept;
//			message.weight = link.weight;
//			idMessageInboundMap.get(link.destinationId).add(message);
//		}
	}

	/*
	 * 
	 */
	public void accept(Link link) {
//		this.testLink = null;
//		if (link.weight < this.bestWeight) {
//			this.bestWeight = link.weight;
//		}

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

	}
	
	/*
	 * 
	 * 
	 */
	public void reportMessage(double weight, Link link) {

	}

	public void changeRoot() {
		Message message = new Message();
		
	}


	
	public void changeRootMessage() {
		changeRoot();
	}

	public void run() {
        Collections.sort(node.potentialLinks);
        while(true) {
            if(node.parent == null) {
                Link mwoe = findMWOE();
                break;
            } else {
                // participate
            }
        }


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

	public void addLink(Link link) {
        outboundMessages.put(link, new LinkedList<Message>());
		node.addLink(link);
	}

	enum State {
		Connect, Initialization, SendMessages, ProcessMessages, End, Find , Found 
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("THREAD %s\n", node.ID));
		for (Link link : node.allLinks) {
			builder.append(String.format("%s\n", link));
		}
		return builder.toString();
	}
}
