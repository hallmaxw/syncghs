/**
 * Created by maxwell on 10/10/15.
 */
import java.awt.TrayIcon.MessageType;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class SyncGHSThread extends Thread {
	private String id;
	private String componentId;
	private Phaser phaser;
	private List<Link> links;
	private State state;
	private int round;
	private int requestedTerminationCount;
	private int terminatedCount;
	private String leaderID;
	private int level ;
	Queue<Message> inboundMessage ;
	public SyncGHSThread(String id, Phaser phaser) {
		this.id = id;
		this.componentId = id;
		this.phaser = phaser;
		this.leaderID =id;
		this.links = new ArrayList<Link>();
		this.state = State.Initialization;
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
		this.level = 0;
		inboundMessage = new SynchronousQueue<Message>();
		
	}

	public SyncGHSThread(String id, List<Link> links, Phaser phaser) {
		this.id = id;
		this.componentId = id;
		this.links = links;
		this.phaser = phaser;
		this.state = State.Initialization;
		round = 1;
		requestedTerminationCount = 0;
		terminatedCount = 0;
	}

	public void sendHelloMessages() {
		for (Link link : links) {
			link.sendMessage(new Message(Message.MessageType.TextMessage, String
					.format("Hello, %s", link.destinationId)));
		}
	}

	public void broadcastMessage(Message msg) {
		for (Link link : links) {
			link.sendMessage(msg);
		}
	}

	public void processMessages() {
		int roundTerminatedCount = 0;
		while (roundTerminatedCount < links.size() - terminatedCount) {
			for (Link link : links) {
				synchronized (link.inboundMessages) {
					while (!link.inboundMessages.isEmpty()) {
						Message msg = link.inboundMessages.remove(0);
						switch (msg.type) {
						case TextMessage:
							print(String.format("Received message:(%s) %s", msg.type.name(), msg.data));
							break;
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
		Link minLink = new Link();
		for (Link link : links) {
			if (link.weight < minLink.weight) {
				minLink = link;
			}
		}

		return minLink;
	}

	/*
	 * wake up function to intiate one 
	 * 
	 */
	/*
	 * (non-Javadoc) read messages and then process
	 */

	public void readMessage() {
		for (Link link : links) {
			synchronized (link.inboundMessages) {
				Message message = link.inboundMessages.remove(0);
				switch (message.type) {
				case Connect:
					
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	
	public void connect(int L , Link link) {
		// TODO: add this feature 
		// if  this node is sleeping then make a wake up call 
		
		if( L < this.level) {
			// change to Branch State 
			link.state = Link.State.Branch;
			
		} else if(link.state == Link.State.Basic) {  // connect
			Message message = new Message();
			message.type = Message.MessageType.Connect;
			message.level = L;
			message.weight = link.weight;
			this.inboundMessage.add(message);			
		}
		
		else { // Initiate
			Message message  = new Message();
			message.type = Message.MessageType.Initiate;
			message.level = L + 1;
			
		}
	}
	public void run() {
		state = State.SendMessages;
		sendHelloMessages();
		broadcastMessage(new Message(Message.MessageType.RoundTermination));
		processMessages();
		waitForRound();

		broadcastMessage(new Message(Message.MessageType.AlgoTerminationRequest));

		while (requestedTerminationCount < links.size()) {
			broadcastMessage(new Message(Message.MessageType.RoundTermination));
			processMessages();
			waitForRound();
		}

		end();
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
		links.add(link);
	}

	enum State {
		Connect ,Initialization, SendMessages, ProcessMessages, End , Find
	}
	
	

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("THREAD %s\n", id));
		for (Link link : links) {
			builder.append(String.format("%s -- %s --> %s\n", id, link.weight, link.destinationId));
		}
		return builder.toString();
	}
}
