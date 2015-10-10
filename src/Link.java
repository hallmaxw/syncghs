/**
 * Created by maxwell on 10/10/15.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Link {
    public List<Message> inboundMessages;
    public List<Message> outboundMessages;

    public Link() {
        inboundMessages = Collections.synchronizedList(new ArrayList<Message>());
        outboundMessages = Collections.synchronizedList(new ArrayList<Message>());
    }

    public Link(List<Message> inboundMessages, List<Message> outboundMessages) {
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

    public static Link GetReverseLink(Link link) {
        Link reverseLink = new Link(link.outboundMessages, link.inboundMessages);
        return reverseLink;
    }
}
