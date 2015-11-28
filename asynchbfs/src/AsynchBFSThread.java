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

	public AsynchBFSThread(String id, Phaser phaser) {
		this.phaser = phaser;
        node = new Node(id, id);
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
        outboundMessages = new HashMap<>();
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
						case RoundTermination:
							roundTerminatedCount++;
							break;
						case AlgoTermination:
							terminatedCount++;
						}
					}
				}
			}
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

	public void run() {
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
