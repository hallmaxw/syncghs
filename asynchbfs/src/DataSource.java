import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Synch GHS Algorithm
 * Group Members:
 * Maxwell Hall
 * Prashant Prakash
 * Shashank Adidamu
 *
 *  Used to read input data
 */
public class DataSource {

	private int numThreads;
	public int root;
    private Map<Integer, Node> nodes;

    public DataSource() {
        nodes = new HashMap<>();
    }

	public void readMetaData(String inputpath) {
		try {
			FileReader input = new FileReader(inputpath);
			BufferedReader br = new BufferedReader(input);
            Scanner inputScanner = new Scanner(br);
            String line = inputScanner.nextLine();

            Scanner metaScanner = new Scanner(line);
            metaScanner.useDelimiter(", ");
            numThreads = metaScanner.nextInt();
            root = metaScanner.nextInt();

            buildNodesMap();
            Node rootNode = nodes.get(root);
            rootNode.parent = rootNode;

            inputScanner.close();
            br.close();
            input.close();
		} catch (Exception ex) {
            ex.printStackTrace();
			System.err.println("Error in reading input File");
		}
	}

    private void buildNodesMap() {
        for(int nodeId = 1; nodeId <= numThreads; nodeId++) {
            nodes.put(nodeId, new Node(String.valueOf(nodeId)));
        }
    }

	public void processConnectivity(String inputpath) {
		try {
			FileReader input = new FileReader(inputpath);
			BufferedReader br = new BufferedReader(input);
            Scanner inputScanner = new Scanner(br);
            // move the scanner to the connectivity section
			inputScanner.nextLine();
			for(int sourceId = 1; sourceId <= numThreads; sourceId++) {

                for (int destinationId = 1; destinationId <= numThreads; destinationId++) {
                    int connectivity = inputScanner.nextInt();
                    // only process the right side of the matrix
                    if(destinationId <= sourceId)
                        continue;
                    if (connectivity == 1) {
                        // add this node as a connection
                        Node srcNode = nodes.get(sourceId);
                        Node destNode = nodes.get(destinationId);
                        srcNode.neighbors.add(destNode);
                        destNode.neighbors.add(srcNode);
                    }
                }
                if(sourceId < numThreads)
				    inputScanner.nextLine();
			}

		} catch (Exception ex) {
            ex.printStackTrace();
			System.err.println("Error in reading input File");
		}
	}

	public int getNumThreads() {
		return numThreads;
	}

	public Collection<Node> getNodes() {
		return nodes.values();
	}


}
