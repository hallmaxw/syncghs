
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Phaser;

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
    //static final int numThreads = 5;

    public static void main(String[] args) {
        
    	String inputPath = "D:\\git\\HackerRankCodes\\SynchGHS\\src\\input.txt";
    	DataSource dsSource = new DataSource();
    	dsSource.readThreadIds(inputPath);
    	Phaser phaser = new Phaser(dsSource.getNumThreads());
        Map<String,SyncGHSThread> threads = new HashMap<String,SyncGHSThread>();
        
        for(int i = 0; i < dsSource.getNumThreads(); i++) {
            threads.put(dsSource.getThreadIds()[i],new SyncGHSThread(dsSource.getThreadIds()[i], phaser));
        }
        
        // building links with weights 
        dsSource.readWeights(inputPath, threads);
        
        /*Link link = new Link(2);
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

		*/

       /* threads.forEach(Thread::start);
        threads.forEach((Thread t) -> {
            try {
                t.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }); */
        System.out.println("All threads finished properly");
    }
}
