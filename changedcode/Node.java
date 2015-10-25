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
    public String componentId;
    public Link parent;
    public List<Link> children;
    public List<Link> potentialLinks; // Links that are potentially merge links
    public List<Link> rejectedLinks;
    public List<Link> allLinks; // list of all links

    public Node(String ID, String componentId) {
        this.ID = ID;
        this.componentId = componentId;
        parent = null;
        children = new ArrayList<Link>();
        potentialLinks = new ArrayList<Link>();
        rejectedLinks = new ArrayList<Link>();
        allLinks = new ArrayList<Link>();
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
