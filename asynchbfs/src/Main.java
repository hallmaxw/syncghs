
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Phaser;

/**
 * Synch GHS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */
public class Main {

    public static void main(String[] args) {
    	String inputPath = args[0];
    	DataSource dsSource = new DataSource();
    	dsSource.readThreadIds(inputPath);
        // phaser is used to manage rounds
    	Phaser phaser = new Phaser(dsSource.getNumThreads());
        Map<String,AsynchBFSThread> threads = new HashMap<>();
        
        for(int i = 0; i < dsSource.getNumThreads(); i++) {
            threads.put(dsSource.getThreadIds()[i],new AsynchBFSThread(dsSource.getThreadIds()[i], phaser));
        }
        
        // building links with weights 
        dsSource.readWeights(inputPath, threads);



        threads.values().forEach(Thread::start);
        threads.values().forEach((Thread t) -> {
            try {
                t.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        if(AsynchBFSThread.DEBUG)
            System.out.println("All threads finished properly");
    }
}
