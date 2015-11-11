/**
 * Synch GHS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Link implements Comparable<Link> {
	public List<Message> inboundMessages;
	public List<Message> outboundMessages;
	public String destinationId;
    public String sourceId;
	public double weight;
	
	public Link(String destinationId, String sourceId, double weight) {
		this.destinationId = destinationId;
        this.sourceId = sourceId;
		this.weight = weight;
        state = State.Basic;
		inboundMessages = Collections.synchronizedList(new ArrayList<>());
		outboundMessages = Collections.synchronizedList(new ArrayList<>());
	}
	
	/*
	 * Default Constructor to initialize the weight to 0
	 */
	public Link() {
		this.weight = Integer.MAX_VALUE;
	}

	public Link(List<Message> inboundMessages, List<Message> outboundMessages, String destinationId, String sourceId, double weight) {
		this.destinationId = destinationId;
        this.sourceId = sourceId;
		this.weight = weight;
		this.inboundMessages = inboundMessages;
		this.outboundMessages = outboundMessages;
        this.state = State.Basic;
	}

	public void sendMessage(Message msg) {
		outboundMessages.add(msg);
	}

	public static Link GetReverseLink(Link link) {
		return new Link(link.outboundMessages, link.inboundMessages, link.sourceId, link.destinationId, link.weight);
	}
	

    @Override
    public String toString() {
        return String.format("%s - %s -> %s", sourceId, weight, destinationId);
    }


    public int compareTo(Link oLink) {
        int cmp = Double.compare(weight, oLink.weight);
        if(cmp == 0) {
            cmp = getMinId().compareTo(oLink.getMinId());
            return cmp == 0 ? getMaxId().compareTo(oLink.getMaxId()) : cmp;
        } else {
            return cmp;
        }
    }

    private String getMaxId() {
        return sourceId.compareTo(destinationId) == 1 ? sourceId : destinationId;
    }

    private String getMinId() {
        return sourceId.compareTo(destinationId) == -1 ? sourceId : destinationId;
    }
}
