import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.Collections.*;

/**
 * Created by maxwell on 10/10/15.
 *
 * Each node says hello to their neighbors.
 * The topology is hard coded to the following:
 *
 *       1-----2-----3
 *       |     |     |
 *       |     |     |
 *       |     |     |
 *       4-----5-----/
 *
 */
public class Main {
    static final int numThreads = 5;

    public static void main(String[] args) {
        Phaser phaser = new Phaser(numThreads);
        List<SyncGHSThread> threads = new ArrayList<SyncGHSThread>();

        for(int i = 0; i < numThreads; i++) {
            threads.add(new SyncGHSThread(i+1, phaser));
        }

        Link link = new Link(2);
        threads.get(0).addLink(link);
        threads.get(1).addLink(Link.GetReverseLink(link, 1));

        link = new Link(4);
        threads.get(0).addLink(link);
        threads.get(3).addLink(Link.GetReverseLink(link, 1));

        link = new Link(5);
        threads.get(1).addLink(link);
        threads.get(4).addLink(Link.GetReverseLink(link, 2));

        link = new Link(5);
        threads.get(3).addLink(link);
        threads.get(4).addLink(Link.GetReverseLink(link, 4));

        link = new Link(3);
        threads.get(1).addLink(link);
        threads.get(2).addLink(Link.GetReverseLink(link, 2));

        link = new Link(3);
        threads.get(4).addLink(link);
        threads.get(2).addLink(Link.GetReverseLink(link, 5));


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
