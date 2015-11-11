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
    int level;
    
    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    enum MessageType {
        MWOEInit,
        MWOEResponse,
        ConnectInit, // data is the Link to merge on
        ConnectRequest, // sent by requester to target
        ConnectResponse, // sent by target to requester
        ConnectForward, // sent from intermediate node to leader. Data is Link to merge on
        ComponentUpdate, // data is new component ID
        ChildUpdateAck, // ComponentUpdate ack
        UpdateQueue, // data is an ArrayList<Link>
        RoundTermination,
        AlgoTerminationRequest,
        AlgoTermination,
        TestRequest, // data is component ID (string)
        TestResponse // data is a boolean
    }
}
