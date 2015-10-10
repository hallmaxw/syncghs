/**
 * Created by maxwell on 10/10/15.
 */
public class Message {
    MessageType type;
    String message;

    public Message(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    enum MessageType {
        Test
    }
}
