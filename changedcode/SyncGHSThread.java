/**
 * Synch GHS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */

import java.util.*;
import java.util.concurrent.Phaser;

public class SyncGHSThread extends Thread {
    static public final boolean DEBUG = false;
	private Phaser phaser;
    private Node node;
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
    private Map<Link, Integer> levelMap;

	public SyncGHSThread(String id, Phaser phaser) {
		this.phaser = phaser;
        node = new Node(id, id);
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
		this.level = 0;
        mwoeResponses = new HashMap<>();
        outboundMessages = new HashMap<>();
        testResponses = new HashMap<>();
        testRequests = new HashMap<>();
        mwoeInitReceived = false;
        connectEdges = new ArrayList<>();
        componentUpdateResponses = new HashMap<>();
        terminate = false;
        connectEdgesUpdates = 0;
        mergeFinished = false;
        levelMap = new HashMap<>();
	}

    /*
        Broadcast a message to all adjacent nodes
     */
	public void broadcastMessage(Message msg) {
		for (Link link : node.allLinks) {
			link.sendMessage(msg);
		}
	}

    /*
        Broadcast a message to children only
     */
    private void broadcastToChildren(Message msg) {
        for (Link link : node.children) {
            link.sendMessage(msg);
        }
    }

    /*
        processMessages is always executed before the end of every round.
        If some data needs to be stored from processed messages, it should be
        contained in an instance variable.

        Messages can be stored in outBoundMessages to be sent at the beginning
        of the next round.
     */
	public void processMessages() {
		int roundTerminatedCount = 0;
		while (roundTerminatedCount < node.allLinks.size() - terminatedCount) {
			for (Link link : node.allLinks) {
				synchronized (link.inboundMessages) {
					while (!link.inboundMessages.isEmpty()) {
						Message msg = link.inboundMessages.remove(0);
						switch (msg.type) {
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
	}

    private void processTestRequest(Link link, Message msg) {
        levelMap.put(link, msg.level);
        if(msg.level > level) {
            // need to wait to respond
            testRequests.put(link, msg);
        } else {
            // respond immediately
            String reqComponent  = (String) msg.data;
            Message responseMsg = new Message(Message.MessageType.TestResponse);
            responseMsg.data = !reqComponent.equals(node.componentId);
            outboundMessages.get(link).add(responseMsg);
        }
    }

    private void processOldTestRequests(){
        Iterator<Map.Entry<Link, Message>> iter = testRequests.entrySet().iterator();
        while(iter.hasNext()) {
            print("Waiting test requests");
            Map.Entry<Link, Message> entry = iter.next();
            Message msg = entry.getValue();
            if(msg.level > level) {
                // still wait
            } else {
                // respond now
                Link link = entry.getKey();
                String reqComponent  = (String) msg.data;
                Message responseMsg = new Message(Message.MessageType.TestResponse);
                responseMsg.data = !reqComponent.equals(node.componentId);
                outboundMessages.get(link).add(responseMsg);
            }
        }
    }


    private void processTestResponse(Link link, Message msg) {
        // test responses need to be acted on later
        testResponses.put(link, msg);
    }

    private void processMWOEResponse(Link link, Message msg) {
        // mwoe responses need to be acted on later
        Link data = (Link) msg.data;
        mwoeResponses.put(link, data);
    }

    /*
        Process a ConnectInit message

        If edge is adjacent, update its state.
        Else, broadcast connect to children
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
            requestMsg.level = level;
            outboundMessages.get(mwoe).add(requestMsg);
        } else {
            // forward to children
            for(Link child: node.children) {
                outboundMessages.get(child).add(msg);
            }
        }
    }

    /*
        Process a ConnectRequest message

        Update the state of the link
        If this is the local leader, add to the connect queue.
        Else, send a ConnectForward message up to the parent
     */
    private void processConnectRequest(Link link, Message msg) {
        levelMap.put(link, msg.level);
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
       Process a ConnectForward message

       If this is the local leader, add to connect queue.
       Else, forward the message to the parent
     */
    private void processConnectForward(Link link, Message msg) {
        if (node.parent == null) {
            Link data = (Link) msg.data;
            connectEdges.add(data);
        } else {
            node.parent.sendMessage(msg);
        }
    }

    /*
        Process a ConnectResponse

        Update the state of the link

        A connect response is only received if this node has
        sent a ConnectRequest. The only state the link should be in is Connect.
     */
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
        if(node.parent == null) {
            // send this node's connect queue to the new leader
            Message queueMsg = new Message(Message.MessageType.UpdateQueue);
            queueMsg.data = connectEdges;
            outboundMessages.get(link).add(queueMsg);
            // reset connectEdges
            connectEdges = null;
            if(DEBUG) {
                print(String.format("From %s\n", link.destinationId));
                print("Set connectEdges to null");
            }
        }

        // update local topology
        if(node.parent != null) {
            node.children.add(node.parent);
        }
        node.parent = link;
        node.children.remove(node.parent);
        node.componentId = newComponentId;
        level = msg.level;
        // forward update to children
        for(Link child: node.children) {
            outboundMessages.get(child).add(msg);
        }
        if(DEBUG)
            print(String.format("CHILDREN: %s", node.children));

        /*
            Some nodes are waiting for a merge to finish.
            Update the flag to notify this node that a merge has finished
         */
        mergeFinished = true;

        // If this node is a leaf, send ChildUpdateAck to parent
        if(node.children.size() == 0) {
            Message ack = new Message(Message.MessageType.ChildUpdateAck);
            outboundMessages.get(node.parent).add(ack);
        }

    }

    private void processChildUpdateAck(Link link, Message msg) {
        if(DEBUG) {
            print(String.format("Received update ACK from %s", link.destinationId));
            print(String.format("PARENT: %s", node.parent));
        }
        componentUpdateResponses.put(link, true);
        // Once all of our children have sent us an ACK, send our parent an ACK
        if(componentUpdateResponses.size() == node.children.size()) {
            if(node.parent != null) {
                if(DEBUG)
                    print(String.format("Forward ACK to %s", node.parent.destinationId));
                outboundMessages.get(node.parent).add(msg);
                componentUpdateResponses.clear();
            }
        }
    }

    private void processAlgoTerminationRequest(Link link, Message msg) {
        requestedTerminationCount++;
        terminate = true;
    }

    private void processUpdateQueue(Link link, Message msg) {
        if(node.parent == null) {
            // local leader
            if(connectEdges == null) {
                connectEdges = new ArrayList<>();
            }
            // merge the old leader's connect queue into mine
            List<Link> oldEdges = (ArrayList<Link>) msg.data;
            connectEdges.addAll(oldEdges);
            connectEdgesUpdates++;
        } else {
            // intermediate node
            // forward the update to my parent
            node.parent.sendMessage(msg);
        }
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

	/*
	 * Get the local minimum weight outgoing edge
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
            waitForRound();
        }

        // remove null mwoe responses (no mwoe found)
        mwoeResponses.entrySet().removeIf(entry -> entry.getValue() == null);

        Link bestCandidate;
        if(node.children.size() > 0 && mwoeResponses.size() > 0) {
            Link childCandidate = Collections.min(mwoeResponses.values());
            bestCandidate = childCandidate.compareTo(localCandidate) < 0 ? childCandidate : localCandidate;
        } else {
            bestCandidate = localCandidate;
        }
        return bestCandidate;
    }

	/*
	 * Connects two components
	 *
	 */
	public void connect(Link link) {
        boolean wasLeader = node.parent == null;
        String newComponentID;
        Link newParent;
        int newLevel;
        if(link.destinationId.compareTo(node.ID) < 1) {
            // other node is new leader
            newComponentID = link.destinationId;
            newParent = link;
            if(levelMap.get(link) == level) {
                newLevel = level + 1;
            } else {
                newLevel = levelMap.get(link);
            }
        } else  {
            // this node is new leader
            newComponentID = node.ID;
            newParent = null;
            if(levelMap.get(link) == level) {
                newLevel = level + 1;
            } else {
                newLevel = level;
            }
        }
        if(!wasLeader)
            node.children.add(node.parent);
        // send update to children
        level = newLevel;
        Message msg = new Message(Message.MessageType.ComponentUpdate, newComponentID);
        msg.level = level;
        broadcastToChildren(msg);
        if(DEBUG) {
            print(String.format("Connecting to  %s", link.destinationId));
            print(String.format("CHILDREN: %s", node.children));
        }

        /*
            If this is the new leader,
            add the new link as a child
         */
        if(newParent == null) {
            node.children.add(link);
        }

        node.parent = null;
        int diff = newParent == null ? 1 : 0;
        // wait for children to ack
        while(componentUpdateResponses.size() < node.children.size() - diff) {
            waitForRound();
        }

        int updatesRequired = 0;
        if(newParent == null) {
            updatesRequired++;
        } else {
            Message queueMsg = new Message(Message.MessageType.UpdateQueue);
            queueMsg.data = connectEdges;
            link.sendMessage(queueMsg);
            connectEdges = null;
        }

        node.parent = newParent;
        node.componentId = newComponentID;
        componentUpdateResponses.clear();
        if(!wasLeader) {
            updatesRequired++;
        }

        // wait for connect edges to update
        while (connectEdgesUpdates < updatesRequired) {
            waitForRound();
        }
        connectEdgesUpdates = 0;
        if(connectEdges != null) {
            // remove the added link from the connect queue
            Iterator<Link> iter = connectEdges.iterator();
            while(iter.hasNext()) {
                Link edge = iter.next();
                if(edge.sourceId.equals(link.sourceId) && edge.destinationId.equals(link.destinationId))
                    iter.remove();
                else if(edge.sourceId.equals(link.destinationId) && edge.destinationId.equals(link.sourceId))
                    iter.remove();
            }
        }
        if(DEBUG)
            print(String.format("Connected on %s\n", link));
        processOldTestRequests();
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
        msg.level = level;
        link.sendMessage(msg);
    }

	public void run() {
        Collections.sort(node.potentialLinks);
        while(!terminate) {
            if(node.parent == null) {
                mergeFinished = false;
                if(DEBUG){
                    print("finding mwoe");
                }
                Link mwoe = findMWOE();
                if(mwoe == null) {
                    break;
                }
                // check if mwoe is local edge
                if (node.allLinks.contains(mwoe)) {
                    sendConnectRequest(mwoe);
                    while (mwoe.state != Link.State.Connected) {
                        waitForRound();
                    }
                    connect(mwoe);
                    node.potentialLinks.remove(mwoe);
                    if (connectEdges != null)
                        if(DEBUG)
                            print(String.format("EDGES: %s", connectEdges));
                } else {
                    // broadcast request to children
                    Message connectInit = new Message(Message.MessageType.ConnectInit, mwoe);
                    broadcastToChildren(connectInit);
                    if(DEBUG)
                        print(String.format("BroadCasting mwoe connect to %s", mwoe));
                    // wait until merge is complete
                    while (!mergeFinished) {
                        waitForRound();
                    }
                    mergeFinished = false;
                }
            }

            while(true) {
                while(node.parent == null && connectEdges.size() > 0) {
                    Link mwoe = connectEdges.get(0);
                    if(node.allLinks.contains(mwoe)) {
                        sendConnectRequest(mwoe);
                        while(mwoe.state != Link.State.Connected) {
                            waitForRound();
                        }
                        connect(mwoe);
                        node.potentialLinks.remove(mwoe);
                        if(DEBUG)
                            print(String.format("%s\n", node));
                        if(connectEdges != null)
                            if(DEBUG)
                                print(String.format("EDGES: %s", connectEdges));
                    } else {
                        // broadcast request to children
                        Message connectInit = new Message(Message.MessageType.ConnectInit, mwoe);
                        broadcastToChildren(connectInit);
                        if(DEBUG)
                            print(String.format("BroadCasting other connect to %s", mwoe));
                        while(!mergeFinished) {
                            waitForRound();
                        }
                        mergeFinished = false;
                        if(DEBUG)
                            print("Merge finished");
                    }
                }
                if(node.parent == null) {
                    break;
                } else {
                    if(terminate)
                        break;
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
                                if(DEBUG)
                                    print(String.format("EDGES: %s", connectEdges));
                            iter.remove();
                        }
                    }
                }
                waitForRound();
            }
        }

        broadcastMessage(new Message(Message.MessageType.AlgoTerminationRequest));
        while(requestedTerminationCount != node.allLinks.size()) {
            waitForRound();
        }
        printAdjacencyList();
        end();
    }

    private void printAdjacencyList() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s adjacent to ", node.ID));
        node.children.forEach(child -> {
            builder.append(child.destinationId);
            builder.append(" ");
        });
        if(node.parent != null) {
            builder.append(node.parent.destinationId);
        }
        System.out.println(builder.toString());
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
        outboundMessages.put(link, new LinkedList<>());
		node.addLink(link);
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
