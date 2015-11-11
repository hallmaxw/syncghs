import java.util.ArrayList;
import java.util.List;

/**
 * Synch GHS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 */

public class Node {
    public String ID;
    public Link parent;
    public List<Link> children;
    public List<Link> allLinks; // list of all links

    public Node(String ID, String componentId) {
        this.ID = ID;
        parent = null;
        children = new ArrayList<>();
        allLinks = new ArrayList<>();
    }

    public void addLink(Link link) {
        potentialLinks.add(link);
        allLinks.add(link);
    }

    public String toString() {
        if(parent == null) {
            return String.format("(ID %s) I'm leader", ID);
        } else {
            return String.format("(ID %s) Parent: %s", ID, parent.destinationId);
        }
    }
}
