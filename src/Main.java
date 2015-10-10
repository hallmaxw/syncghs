import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by maxwell on 10/10/15.
 */
public class Main {
    static final int numThreads = 10;

    public static void main(String[] args) {
        List<SyncGHSThread> threads = new ArrayList<SyncGHSThread>();
        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        for(int i = 0; i < numThreads; i++) {
            threads.add(new SyncGHSThread(i+1, barrier));
        }
        threads.forEach(Thread::start);
        threads.forEach((Thread t) -> {
            try {
                t.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
