/**
 * Created by maxwell on 10/10/15.
 */
public class Message {
    MessageType type;
    Object data;
    int level;
    double weight;
    State state;
    String id;

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
        TextMessage,
        MWOEInit,
        MWOEResponse,
        RoundTermination,
        AlgoTerminationRequest,
        AlgoTermination,
        Connect,
        Initiate,
        TestRequest,
        TestResponse,
        Reject,
        Accept,
        ReportMessage,
        ChangeRootMessage
    }
    
    enum State {
    	Find , Found 
    }
}
