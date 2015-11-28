import java.util.*;

/**
 * Asynch BFS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */

public class Node {
    public String ID;
    public Node parent;
    public Map<Node, List<Message>> inboundMessages;
    public List<Node> neighbors;
    int distance;

    public Node(String ID) {
        this.ID = ID;
        parent = null;
        inboundMessages = Collections.synchronizedMap(new HashMap<>());
        neighbors = new ArrayList<>();
        distance = Integer.MAX_VALUE;
    }

    public String toString() {
        return String.format("(ID %s) Parent: %s", ID, parent.ID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (!ID.equals(node.ID)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
