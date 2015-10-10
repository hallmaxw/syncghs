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
    static final int numThreads = 5;

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        List<SyncGHSThread> threads = new ArrayList<SyncGHSThread>();

        for(int i = 0; i < numThreads; i++) {
            threads.add(new SyncGHSThread(i+1, barrier));
        }

        Link link = new Link();
        threads.get(0).addLink(link);
        threads.get(1).addLink(Link.GetReverseLink(link));

        link = new Link();
        threads.get(0).addLink(link);
        threads.get(3).addLink(Link.GetReverseLink(link));

        link = new Link();
        threads.get(1).addLink(link);
        threads.get(4).addLink(Link.GetReverseLink(link));

        link = new Link();
        threads.get(3).addLink(link);
        threads.get(4).addLink(Link.GetReverseLink(link));

        link = new Link();
        threads.get(1).addLink(link);
        threads.get(2).addLink(Link.GetReverseLink(link));

        link = new Link();
        threads.get(4).addLink(link);
        threads.get(2).addLink(Link.GetReverseLink(link));


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
