import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Node(String ID, String componentId) {
        this.ID = ID;
        parent = null;
        inboundMessages = new HashMap<>();
        neighbors = new ArrayList<>();
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
