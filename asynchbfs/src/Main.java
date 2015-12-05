
import java.util.*;
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
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
       // printAdjacencyLists(nodes);
        System.out.format("The root is process %d\n", dataSource.root);
        System.out.format("Process Parent Distance\n");
        for(Node node: nodes) {
            int process = Integer.parseInt(node.ID);
            int parent = Integer.parseInt(node.parent.ID);
            if(process == parent) {
                parent = -1;
            }
            int distance = node.distance;
            System.out.format("%-8d%-7d%-8d\n", process, parent, distance);
        }
        if(AsynchBFSThread.DEBUG)
            System.out.println("All threads finished properly");
    }

    private static void printAdjacencyLists(List<Node> nodes) {
        Map<Node, Set<Node>> map = new HashMap<>();
        for(Node node: nodes) {
            Set<Node> thisList = getAdjacencyList(map, node);
            Set<Node> parentList = getAdjacencyList(map, node.parent);
            thisList.add(node.parent);
            parentList.add(node);
        }
        for(Map.Entry<Node, Set<Node>> nodeEntry: map.entrySet()) {
            Node node = nodeEntry.getKey();
            Set<Node> adjacentNodes = nodeEntry.getValue();
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("NODE %s adjacency list: ", node.ID));
            for(Node adjacentNode: adjacentNodes) {
                builder.append(String.format("%s ", adjacentNode.ID));
            }
            System.out.println(builder.toString());
        }
    }

    private static Set<Node> getAdjacencyList(Map<Node, Set<Node>> map, Node node) {
        if(map.containsKey(node)) {
            return map.get(node);
        } else {
            Set<Node> adjacencyList = new HashSet<>();
            map.put(node, adjacencyList);
            return adjacencyList;
        }
    }


}
