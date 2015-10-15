/**
 * Created by maxwell on 10/10/15.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SyncGHSThread extends Thread {
    private int id;
    private int componentId;
    private Phaser phaser;
    private List<Link> links;
    private State state;
    private int round;
    private int requestedTerminationCount;
    private int terminatedCount;


    public SyncGHSThread(int id, Phaser phaser) {
        this.id = id;
        this.componentId = id;
        this.phaser = phaser;
        this.links = new ArrayList<Link>();
        this.state = State.Initialization;
        round = 1;
        requestedTerminationCount = 0;
        terminatedCount = 0;
    }

    public SyncGHSThread(int id, List<Link> links, Phaser phaser) {
        this.id = id;
        this.componentId = id;
        this.links = links;
        this.phaser = phaser;
        this.state = State.Initialization;
        round = 1;
        requestedTerminationCount = 0;
        terminatedCount = 0;
    }

    public void sendHelloMessages() {
        for(Link link: links) {
            link.sendMessage(new Message(Message.MessageType.TextMessage, String.format("Hello, %d", link.destinationId)));
        }
    }

    public void sendEvenMessages() {
        for(Link link: links) {
            link.sendMessage(new Message(Message.MessageType.TextMessage, String.format("Hey %d, I'm even!", link.destinationId)));
        }
    }

    public void sendRoundTerminationMessages() {
        for(Link link: links) {
            link.sendMessage(new Message(Message.MessageType.RoundTermination));
        }
    }

    public void sendAlgoTerminationRequestMessages() {
        for(Link link: links) {
            link.sendMessage(new Message(Message.MessageType.AlgoTerminationRequest));
        }
    }

    public void sendAlgoTerminationMessages() {
        for(Link link: links) {
            link.sendMessage(new Message(Message.MessageType.AlgoTermination));
        }
    }


    public void processMessages() {
        int terminatedCount = 0;
        while(terminatedCount < links.size() - terminatedCount) {
            for (Link link : links) {
                synchronized (link.inboundMessages) {
                    while (!link.inboundMessages.isEmpty()) {
                        Message msg = link.inboundMessages.remove(0);
                        switch (msg.type) {
                            case TextMessage:
                                print(String.format("Received message:(%s) %s", msg.type.name(), msg.data));
                                break;
                            case RoundTermination:
                                terminatedCount++;
                                break;
                            case AlgoTerminationRequest:
                                requestedTerminationCount++;
                                break;
                            case AlgoTermination:
                                terminatedCount++;
                        }
                    }
                }
            }
        }
        print("Processed messages");
    }

    public void end() {
        sendAlgoTerminationMessages();
        print("Finished");
        phaser.arriveAndDeregister();
    }

    public void print(String msg) {
        System.out.format("ID %d (round %d): %s\n", id, round, msg);
    }

    public void run() {
        state = State.SendMessages;
        sendHelloMessages();
        sendRoundTerminationMessages();
        processMessages();
        waitForRound();


        if(id % 2 == 0) {
            sendEvenMessages();
            sendRoundTerminationMessages();
            processMessages();
            waitForRound();
        }

        sendAlgoTerminationRequestMessages();

        while(requestedTerminationCount < links.size()) {
            sendRoundTerminationMessages();
            processMessages();
            waitForRound();
        }

        end();
    }

    public void waitForRound() {
        try {
            phaser.arriveAndAwaitAdvance();
            round++;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addLink(Link link) {
        links.add(link);
    }

    enum State {
        Initialization, SendMessages, ProcessMessages, End
    }
}
