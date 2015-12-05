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
    public List<Message> inboundMessages;
    public List<Node> neighbors;
    int distance;

    public Node(String ID) {
        this.ID = ID;
        parent = null;
        inboundMessages = Collections.synchronizedList(new ArrayList<>());
        neighbors = new ArrayList<>();
        distance = Integer.MAX_VALUE;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(parent != null) {
            builder.append(String.format("(ID %s) Parent: %s\n", ID, parent.ID));
        } else {
            builder.append(String.format("(ID %s) No Parent\n", ID));
        }

        builder.append("NEIGHBORS:\n");
        for(Node neighbor: neighbors) {
            builder.append(String.format("ID %s\n", neighbor.ID));
        }

        return builder.toString();
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
