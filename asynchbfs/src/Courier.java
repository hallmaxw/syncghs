import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Asynch BFS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 *
 * Courier handles sending messages between nodes
 */
public class Courier {
	// Map declaration to handle message Queue for every Node
    private Map<Node,Queue<Message>> messageQueue = new HashMap<Node, Queue<Message>>();
	
	/*
        Add a message to the message queue of the given node
     */
    public void addMessage(Message msg, Node destination) {
    	// if destination Node is already there 
    	// ## equals and hascode method needs to overidden in Node class 
    	if(messageQueue.containsKey(destination)) {
    		Queue<Message> tempQueue = messageQueue.get(destination);
    		tempQueue.add(msg);
    		messageQueue.put(destination, tempQueue); 		
    		
    	}else {
    		Queue<Message> tempQueue = new LinkedList<Message>();
    		tempQueue.add(msg);
    		messageQueue.put(destination, tempQueue);
    	}
    }

    /*
        Send messages for the given round
     */
    public void sendMessages(int round) {
    	// keep polling message from the message Queue and put that into the destination Node inbound list
    	for (Node node : messageQueue.keySet()) {
			Message pollMessage = messageQueue.get(node).poll();
    		while(pollMessage != null && pollMessage.round <= round) { // round condition keep polling messages for round less than input round 
    			pollMessage = messageQueue.get(node).poll();
    			// ## todo - why inbound message has key as Node ? it can be only node id also right? 
    			List<Message> message = node.inboundMessages.get(node); 
    			message.add(pollMessage);
    			node.inboundMessages.put(node, message);
    		}
		}
    }
}
