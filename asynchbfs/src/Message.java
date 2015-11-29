/**
 * Synch GHS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */

public class Message {
    MessageType type;
    Object data;
    int round;
    
    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    enum MessageType {
        RoundTermination,
        AlgoTerminationRequest,
        AlgoTermination,
        DistanceUpdate
    }
}
