/**
 * Created by maxwell on 10/10/15.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SyncGHSThread extends Thread {
    private int id;
    private CyclicBarrier barrier;
    private List<Link> links;
    private State state;

    public SyncGHSThread(int id, CyclicBarrier barrier) {
        this.id = id;
        this.barrier = barrier;
        this.links = new ArrayList<Link>();
        this.state = State.Initialization;
    }

    public SyncGHSThread(int id, List<Link> links, CyclicBarrier barrier) {
        this.id = id;
        this.links = links;
        this.barrier = barrier;
        this.state = State.Initialization;
    }

    public void sendMessages() {
        String hello = String.format("Hello, I'm thread %d", id);
        for(Link link: links) {
            link.sendMessage(new Message(Message.MessageType.Test, hello));
        }
        print("Sent messages");
        state = State.ReadMessages;
    }

    public void readMessages() {
        for(Link link: links) {
            synchronized (link.inboundMessages) {
                while(!link.inboundMessages.isEmpty()) {
                    Message msg = link.inboundMessages.remove(0);
                    print(String.format("Received message: %s", msg.message));
                }
            }
        }
        state = State.End;
    }

    public void end() {
        print("Finished");
    }

    public void print(String msg) {
        System.out.format("%d: %s\n", id, msg);
    }

    public void run() {
        state = State.SendMessages;
        while(true) {
            switch(state) {
                case SendMessages:
                    sendMessages();
                    break;
                case ReadMessages:
                    readMessages();
                    break;
                case End:
                    end();
                    return;
            }
            waitForRound();
        }
    }

    public void waitForRound() {
        try {
            barrier.await();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addLink(Link link) {
        links.add(link);
    }

    enum State {
        Initialization, SendMessages, ReadMessages, End
    }
}
