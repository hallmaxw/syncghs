/**
 * Created by maxwell on 10/10/15.
 */
import java.util.List;
import java.util.concurrent.*;

public class SyncGHSThread extends Thread {
    private int id;
    private CyclicBarrier barrier;
    private Link link;
    private State state;

    public SyncGHSThread(int id, Link link, CyclicBarrier barrier) {
        this.id = id;
        this.link = link;
        this.barrier = barrier;
        this.state = State.Initialization;
    }

    public void sendMessages() {
        print("Sending message");
        Message msg = new Message(Message.MessageType.Test, String.format("Hello, I'm thread %d", id));
        link.sendMessage(msg);
        state = State.ReadMessages;
    }

    public void readMessages() {
        Message msg = link.getMessage();
        print(String.format("Received message: %s", msg.message));
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

    enum State {
        Initialization, SendMessages, ReadMessages, End
    }
}
