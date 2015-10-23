/**
 * Created by maxwell on 10/10/15.
 *
 * Each node says hello to their neighbors.
 * The topology is hard coded to the following:
 *
 *       1-----2-----3
 *       |     |     |
 *       |     |     |
 *       |     |     |
 *       4-----5-----/
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Link {
	public List<Message> inboundMessages;
	public List<Message> outboundMessages;
	public String destinationId;
	public double weight;
	public State state ;

	public Link(String destinationId, double weight) {
		this.destinationId = destinationId;
		this.weight = weight;
		inboundMessages = Collections.synchronizedList(new ArrayList<Message>());
		outboundMessages = Collections.synchronizedList(new ArrayList<Message>());
	} 
	
	/*
	 * Default Constructor to initialize the weight to 0
	 */
	public Link() {
		this.weight = Integer.MAX_VALUE;
	}

	public Link(List<Message> inboundMessages, List<Message> outboundMessages, String destinationId, double weight) {
		this.destinationId = destinationId;
		this.weight = weight;
		this.inboundMessages = inboundMessages;
		this.outboundMessages = outboundMessages;
	}

	public void sendMessage(Message msg) {
		outboundMessages.add(msg);
	}

	public Message peekMessage() {
		return inboundMessages.get(0);
	}

	public Message getMessage() {
		return inboundMessages.remove(0);
	}

	public static Link GetReverseLink(Link link, String destinationId) {
		Link reverseLink = new Link(link.outboundMessages, link.inboundMessages, destinationId, link.weight);
		return reverseLink;
	}
	
	enum State {
        Basic, Branch , Rejected
    }
}