/**
 * Created by maxwell on 10/10/15.
 */
public class Message {
    MessageType type;
    Object data;
    int level;
    double weight;

    public Message() {
    	
    }
    
    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    enum MessageType {
        TextMessage, RoundTermination, AlgoTerminationRequest, AlgoTermination, Connect , Initiate
    }
}
