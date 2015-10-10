/**
 * Created by maxwell on 10/10/15.
 */
import java.util.concurrent.*;

public class SyncGHSThread extends Thread {
    private int id;
    private CyclicBarrier barrier;

    public SyncGHSThread(int id, CyclicBarrier barrier) {
        this.id = id;
        this.barrier = barrier;
    }

    public void run() {
        System.out.format("Hello, I'm thread %d\n", id);
        try {
            barrier.await();
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.format("Hello again, I'm thread %d\n", id);
    }
}
