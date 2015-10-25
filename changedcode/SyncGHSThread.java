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
    private Map<Link, Message> testRequests; //test requests that we can't respond to yet
    private boolean mwoeInitReceived;
    private List<Link> connectEdges; // list of edges to connect on. Should be empty if not leader
    private Map<Link, Boolean> componentUpdateResponses; // map to store which links have sent an ack
    private boolean terminate;
    private int connectEdgesUpdates;
    private boolean mergeFinished;

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
        testRequests = new HashMap<Link, Message>();
        mwoeInitReceived = false;
        connectEdges = new ArrayList<Link>();
        componentUpdateResponses = new HashMap<Link, Boolean>();
        terminate = false;
        connectEdgesUpdates = 0;
        mergeFinished = false;
	}

	public void broadcastMessage(Message msg) {
		for (Link link : node.allLinks) {
			link.sendMessage(msg);
		}
	}

    private void broadcastToChildren(Message msg) {
        for (Link link : node.children) {
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
                            break;
                        case MWOEInit:
                            mwoeInitReceived = true;
                            break;
                        case MWOEResponse:
                            processMWOEResponse(link, msg);
                            break;
                        case ConnectInit:
                            processConnectInit(link, msg);
                            break;
                        case ConnectRequest:
                            processConnectRequest(link, msg);
                            break;
                        case ConnectResponse:
                            processConnectResponse(link, msg);
                            break;
                        case ConnectForward:
                            processConnectForward(link, msg);
                            break;
                        case ComponentUpdate:
                            processComponentUpdate(link, msg);
                            break;
                        case ChildUpdateAck:
                            processChildUpdateAck(link, msg);
                            break;
                        case UpdateQueue:
                            processUpdateQueue(link, msg);
                            break;
						case RoundTermination:
							roundTerminatedCount++;
							break;
						case AlgoTerminationRequest:
							processAlgoTerminationRequest(link, msg);
							break;
						case AlgoTermination:
							terminatedCount++;
						}
					}
				}
			}
		}
		//print("Processed messages");
	}

    private void processTestRequest(Link link, Message msg) {
        if(msg.level > level) {
            // need to wait to respond
            testRequests.put(link, msg);
        } else {
            // respond immediately
            String reqComponent  = (String) msg.data;
            Message responseMsg = new Message(Message.MessageType.TestResponse);
            if(reqComponent.equals(node.componentId)) {
                responseMsg.data = false;
            } else {
                responseMsg.data = true;
            }
            outboundMessages.get(link).add(responseMsg);
        }
    }

    private void processTestResponse(Link link, Message msg) {
        testResponses.put(link, msg);
    }

    private void processMWOEResponse(Link link, Message msg) {
        Link data = (Link) msg.data;
        mwoeResponses.put(link, data);
    }

    /*
        tell children to merge on the provided edge
     */
    private void processConnectInit(Link link, Message msg) {
        Link mwoe = (Link) msg.data;
        if(node.allLinks.contains(mwoe)) {
            switch(mwoe.state) {
                case Basic:
                    mwoe.state = Link.State.Connect;
                    break;
                case Connect:
                    mwoe.state = Link.State.Connected;
                    break;
                case Connected:
                    print("Something weird happened");
                    break;
            }
            // send a connect request
            Message requestMsg = new Message(Message.MessageType.ConnectRequest);
            outboundMessages.get(mwoe).add(requestMsg);
        } else {
            // forward to children
            for(Link child: node.children) {
                outboundMessages.get(child).add(msg);
            }
        }
    }

    /*

     */
    private void processConnectRequest(Link link, Message msg) {
        switch(link.state) {
            case Basic:
                link.state = Link.State.Connect;
                break;
            case Connect:
                link.state = Link.State.Connected;
                break;
            case Connected:
                print("Something weird happened");
                break;
        }
        // if leader, add to list
        if (node.parent == null) {
            connectEdges.add(link);
        } else {
            Message forwardMsg = new Message(Message.MessageType.ConnectForward, link);
            node.parent.sendMessage(forwardMsg);
        }
    }

    /*
        Forward the connect to the leader
     */
    private void processConnectForward(Link link, Message msg) {
        if (node.parent == null) {
            connectEdges.add(link);
        } else {
            node.parent.sendMessage(msg);
        }
    }

    private void processConnectResponse(Link link, Message msg) {
        switch(link.state) {
            case Basic:
                print("Someting weird happened");
                break;
            case Connect:
                link.state = Link.State.Connected;
                break;
            case Connected:
                print("Something weird happened");
                break;
        }
    }

    private void processComponentUpdate(Link link, Message msg) {
        String newComponentId = (String) msg.data;
        if(!node.componentId.equals(newComponentId)) {
            if(node.parent == null) {
                Message queueMsg = new Message(Message.MessageType.UpdateQueue);
                queueMsg.data = connectEdges;
                // reset connectEdges
                connectEdges = null;
                print(String.format("From %s\n", link.destinationId));
                print("Set connectEdges to null");
                outboundMessages.get(link).add(queueMsg);
            }

            // forward to all adjacent nodes
            if(node.parent != null) {
                node.children.add(node.parent);
            }
            node.parent = link;
            node.children.remove(node.parent);
            for(Link child: node.children) {
                outboundMessages.get(child).add(msg);
            }
            print(String.format("CHILDREN: %s", node.children));
            // update this node
            node.componentId = newComponentId;

            if(node.children.size() == 0) {
                // this is a leaf
                Message ack = new Message(Message.MessageType.ChildUpdateAck);
                outboundMessages.get(node.parent).add(ack);
            }
        }
    }

    private void processChildUpdateAck(Link link, Message msg) {
        print(String.format("Received update ACK from %s", link.destinationId));
        componentUpdateResponses.put(link, true);
        print(String.format("UpdateResponses: %d", componentUpdateResponses.size()));
        print(String.format("PARENT: %s", node.parent));
        if(componentUpdateResponses.size() == node.children.size()) {
            if(node.parent != null) {
                mergeFinished = true;
                print(String.format("Forward ACK to %s", node.parent.destinationId));
                outboundMessages.get(node.parent).add(msg);
                componentUpdateResponses.clear();
            }
        }
    }

    private void processAlgoTerminationRequest(Link link, Message msg) {
        for(Link child : node.children) {
            outboundMessages.get(child).add(msg);
        }
        terminate = true;
    }

    private void processUpdateQueue(Link link, Message msg) {
        if(node.parent == null) {
            if(connectEdges == null) {
                connectEdges = new ArrayList<Link>();
            }
            List<Link> oldEdges = (ArrayList<Link>) msg.data;
            connectEdges.addAll(oldEdges);
            connectEdgesUpdates++;
        } else {
            node.parent.sendMessage(msg);
        }
    }

	public void end() {
        broadcastMessage(new Message(Message.MessageType.AlgoTermination));
		print("Finished");
        print(String.format("%s\n", node));
		phaser.arriveAndDeregister();
	}

	public void print(String msg) {
		System.out.format("ID %s (round %d): %s\n", node.ID, round, msg);
	}

	/*
	 * Get the minimum weight associated with the node
	 */
	public Link findLocalMinEdge() {
        Iterator<Link> iter = node.potentialLinks.iterator();
        while(iter.hasNext()) {
            Link candidate = iter.next();
            boolean isOutgoing = testLink(candidate);
            if(isOutgoing) {
                return candidate;
            } else {
                iter.remove();
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
            print(String.format("MWOE_WAIT GOAL: %d STATUS: %d", node.children.size(), mwoeResponses.size()));
            waitForRound();
        }
        Link bestCandidate = null;
        if(node.children.size() > 0) {
            Link childCandidate = Collections.min(mwoeResponses.values());
            bestCandidate = childCandidate.compareTo(localCandidate) < 0 ? childCandidate : localCandidate;
        } else {
            bestCandidate = localCandidate;
        }
        return bestCandidate;
    }

	/*
	 * (non-Javadoc)
	 * Attempts to merge two trees
	 * @see java.lang.Thread#run()
	 */

	public void connect(Link link) {
        boolean wasParent = node.parent == null;
        String newComponentID = "";
        Link newParent = null;
        if(link.destinationId.compareTo(node.ID) < 1) {
            // other node is leader
            newComponentID = link.destinationId;
            newParent = link;
        } else  {
            // this node is leader
            newComponentID = node.ID;
            newParent = null;
        }
        if(node.parent != null)
            node.children.add(node.parent);
        // send update to children
        Message msg = new Message(Message.MessageType.ComponentUpdate, newComponentID);
        print(String.format("Connecting to  %s", link.destinationId));
        broadcastToChildren(msg);
        print(String.format("CHILDREN: %s", node.children));

        // add the new link as a child
        if(newParent == null) {
            node.children.add(link);
        }

        node.parent = null;
        int diff = newParent == null ? 1 : 0;
        // wait for children to ack
        while(componentUpdateResponses.size() < node.children.size() - diff) {
            print(String.format("waiting for children GOAL: %d STATUS: %d", node.children.size()-diff, componentUpdateResponses.size()));
            waitForRound();
        }
        node.parent = newParent;
        componentUpdateResponses.clear();
        int updatesRequired = 0;
        if(!wasParent) {
            updatesRequired++;
        }
        if(newParent == null) {
            updatesRequired++;
        } else {
            Message queueMsg = new Message(Message.MessageType.UpdateQueue);
            queueMsg.data = connectEdges;
            link.sendMessage(queueMsg);
            connectEdges = null;
        }
        // wait for connect edges to update
        while (connectEdgesUpdates < updatesRequired) {
            waitForRound();
        }
        connectEdgesUpdates = 0;

        print(String.format("Connected on %s\n", link));
	}


    private void sendConnectRequest(Link link) {
        switch (link.state) {
            case Basic:
                link.state = Link.State.Connect;
                break;
            case Connect:
                link.state = Link.State.Connected;
                break;
            case Connected:
                print("Something weird happened");
                break;
        }
        Message msg = new Message(Message.MessageType.ConnectRequest);
        link.sendMessage(msg);
    }

	public void run() {
        Collections.sort(node.potentialLinks);
        while(true) {
            print(String.format("PARENT: %s", node.parent));
            if(node.parent == null) {
                Link mwoe = findMWOE();
                print("I've FOUND THE MWOE MWAHAHA");
                if(mwoe == null) {
                    print("ANSWER ACHIEVED YAYAYYAY");
                    break;
                }
                // check if mwoe is local edge
                if (node.allLinks.contains(mwoe)) {
                    sendConnectRequest(mwoe);
                    while (mwoe.state != Link.State.Connected) {
                        waitForRound();
                    }
                    connectEdges.remove(mwoe);
                    connect(mwoe);
                    node.potentialLinks.remove(mwoe);
                    if (connectEdges != null)
                        print(String.format("EDGES: %s", connectEdges));
                } else {
                    // broadcast request to children
                    Message connectInit = new Message(Message.MessageType.ConnectInit, mwoe);
                    broadcastToChildren(connectInit);
                    print(String.format("BroadCasting connect to %s", mwoe));
                    // wait until merge is complete
                    while (!mergeFinished) {
                        waitForRound();
                    }

                }
            }

            while(true) {
                while(node.parent == null && connectEdges.size() > 0) {
                    Link mwoe = connectEdges.remove(0);
                    if(node.allLinks.contains(mwoe)) {
                        sendConnectRequest(mwoe);
                        while(mwoe.state != Link.State.Connected) {
                            waitForRound();
                        }
                        connect(mwoe);
                        node.potentialLinks.remove(mwoe);
                        print(String.format("%s\n", node));
                        if(connectEdges != null)
                            print(String.format("EDGES: %s", connectEdges));
                    } else {
                        // broadcast request to children
                        Message connectInit = new Message(Message.MessageType.ConnectInit, mwoe);
                        broadcastToChildren(connectInit);
                        print(String.format("BroadCasting connect to %s", mwoe));
                        while(!mergeFinished) {
                            waitForRound();
                        }
                    }
                }
                if(node.parent == null) {
                    break;
                } else {
                    if(mwoeInitReceived) {
                        mwoeInitReceived = false;
                        Link mwoe = findMWOE();
                        // send candidate to parent
                        Message mwoeResponse = new Message(Message.MessageType.MWOEResponse, mwoe);
                        node.parent.sendMessage(mwoeResponse);
                    }

                    Iterator<Link> iter = node.potentialLinks.iterator();
                    while(iter.hasNext()) {
                        Link link = iter.next();
                        // check if a link is marked as connected
                        // if so, connect on it
                        if(link.state == Link.State.Connected) {
                            connect(link);
                            if(connectEdges != null)
                                print(String.format("EDGES: %s", connectEdges));
                            iter.remove();
                        }
                    }
                }
                waitForRound();
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
