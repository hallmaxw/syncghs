/**
 * Asynch BFS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */

public class Message {
    Node source;
    MessageType type;
    Object data;
    int round;
    
    public Message(Node source, MessageType type) {
        this.source = source;
        this.type = type;
        this.data = null;
    }

    public Message(Node source, MessageType type, Object data) {
        this.source = source;
        this.type = type;
        this.data = data;
    }

    public String toString() {
        return String.format("SOURCE: %s TYPE: %s ROUND: %d\n", source.ID, type.name(), round);
    }

    enum MessageType {
        RoundTermination,
        AlgoTermination,
        DistanceUpdate,
        Ack
    }
}
