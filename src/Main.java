import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.Collections.*;

/**
 * Created by maxwell on 10/10/15.
 */
public class Main {
    static final int numThreads = 2;

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        List<SyncGHSThread> threads = new ArrayList<SyncGHSThread>();
        Link link = new Link();
        threads.add(new SyncGHSThread(1, link, barrier));
        threads.add(new SyncGHSThread(2, Link.GetReverseLink(link), barrier));

        threads.forEach(Thread::start);
        threads.forEach((Thread t) -> {
            try {
                t.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("All threads finished properly");
    }
}
