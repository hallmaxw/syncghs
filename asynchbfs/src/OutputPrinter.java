import java.util.*;

/**
 * Created by maxwell on 12/6/15.
 */
public class OutputPrinter {

    public static void printRoot(int root) {
        System.out.format("The root is process %d\n", root);
    }
    public static void printParentTable(List<Node> nodes) {
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
    }

    public static void printAdjacencyLists(List<Node> nodes) {
        Map<Node, Set<Node>> map = new HashMap<>();
        for(Node node: nodes) {
            Set<Node> thisList = getAdjacencyList(map, node);
            Set<Node> parentList = getAdjacencyList(map, node.parent);
            if(thisList == parentList) {
                continue;
            }
            thisList.add(node.parent);
            parentList.add(node);
        }

        System.out.println("Adjacency List:");
        for(Map.Entry<Node, Set<Node>> nodeEntry: map.entrySet()) {
            Node node = nodeEntry.getKey();
            Set<Node> adjacentNodes = nodeEntry.getValue();
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("NODE %-3s adjacency list: ", node.ID));
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
