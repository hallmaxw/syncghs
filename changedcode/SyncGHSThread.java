/**
 * Created by maxwell on 10/10/15.
 */
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Phaser;
import java.util.concurrent.SynchronousQueue;

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
	private Queue<Message> inboundMessage ;
	private Link testLink;
	private Map<String,Queue<Message>> idMessageInboundMap;
	
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
		idMessageInboundMap  = new HashMap<String, Queue<Message>>();
		testLink = new Link();
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
			message.state = Message.State.Find;
			message.weight = link.weight;
			this.inboundMessage.add(message);
			
		}
	}
	
	/*
	 * 
	 * Intiate function
	 */
	public void initiate(int L , String id , State state) {
		this.level  = L; 
		this.id = id;
		this.state = State.Find;
		for (Link link : links) {
			if(link.state == Link.State.Branch) { // 1 is for Branch
				Message message = new Message();
				message.type = Message.MessageType.Initiate;
				message.level = L ;
				message.id = id;
				message.weight = link.weight;
				idMessageInboundMap.get(link.destinationId).add(message);
			}
		}
		
		if(state == State.Find) {
			// call test function
		}
		
	}
	
	
	public void test() {
		double  min = Double.MAX_VALUE ;
		Link minLink  = null;
		for (Link link : links) {
			if(link.state  ==  Link.State.Basic) {
				if(min > link.weight) {
					minLink = link;
					min = link.weight;
				}
			}
		}
		
		if(min != Double.MAX_VALUE) {
			this.testLink = minLink;
			Message message = new Message();
			message.type = Message.MessageType.TestMessage;
			message.level = this.level;
			message.id =this.id;
			message.weight = minLink.weight;
			idMessageInboundMap.get(minLink.destinationId).add(message);
		} else {
			this.testLink = null;
			// call function report
		}
		
		
	}
	
	
	public void testMessage (int L, int id , int j) {
		
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
