
import java.util.*;
import java.util.concurrent.Phaser;

/**
 * Asynch BFS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */
public class Main {


    public static void main(String[] args) {
    	String inputPath = args[0];
    	DataSource dataSource = new DataSource();
        dataSource.readMetaData(inputPath);
        dataSource.processConnectivity(inputPath);
        List<Node> nodes =  new ArrayList<>(dataSource.getNodes());
        // phaser is used to manage rounds
    	Phaser phaser = new Phaser(dataSource.getNumThreads());
        Map<String, AsynchBFSThread> threads = new HashMap<>();

        for(Node node: nodes) {
            threads.put(node.ID, new AsynchBFSThread(node, phaser));
        }

        threads.values().forEach(Thread::start);
        threads.values().forEach((Thread t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        OutputPrinter.printRoot(dataSource.root);
        OutputPrinter.printParentTable(nodes);
        System.out.println();
        OutputPrinter.printAdjacencyLists(nodes);
        if(AsynchBFSThread.DEBUG)
            System.out.println("All threads finished properly");
    }




}
