
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
        //String inputPath = "D:\\git\\HackerRankCodes\\SynchGHS\\src\\input.txt";
    	String inputPath = "/Users/maxwell/syncghs/input-file.txt";
    	DataSource dsSource = new DataSource();
    	dsSource.readThreadIds(inputPath);
    	Phaser phaser = new Phaser(dsSource.getNumThreads());
        Map<String,SyncGHSThread> threads = new HashMap<String,SyncGHSThread>();
        
        for(int i = 0; i < dsSource.getNumThreads(); i++) {
            threads.put(dsSource.getThreadIds()[i],new SyncGHSThread(dsSource.getThreadIds()[i], phaser));
        }
        
        // building links with weights 
        dsSource.readWeights(inputPath, threads);
        threads.values().forEach((SyncGHSThread t) -> {
            System.out.println(t);
        });


        threads.values().forEach(Thread::start);
        threads.values().forEach((Thread t) -> {
            try {
                t.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }); 
        System.out.println("All threads finished properly");
    }
}
