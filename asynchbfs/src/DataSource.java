import java.io.BufferedReader;
import java.io.FileReader;
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
	private String[] threadIds;

	public void readThreadIds(String inputpath) {
		try {
			FileReader input = new FileReader(inputpath);
			BufferedReader br = new BufferedReader(input);
            Scanner inputScanner = new Scanner(br);
            numThreads = inputScanner.nextInt();
            inputScanner.nextLine();
            threadIds = new String[numThreads];

            for(int i = 0; i < threadIds.length; i++) {
                threadIds[i] = inputScanner.next();
            }
            inputScanner.nextLine();

		} catch (Exception ex) {
			System.err.println("Error in reading input File");
		}
	}

	public void readWeights(String inputpath, Map<String, AsynchBFSThread> threads) {
		try {
			FileReader input = new FileReader(inputpath);
			BufferedReader br = new BufferedReader(input);
            Scanner inputScanner = new Scanner(br);
            // move the scanner to the weights
			inputScanner.nextLine();
            inputScanner.nextLine();
			for(int sourceIndex = 0; sourceIndex < threads.size(); sourceIndex++) {

                for (int destinationIndex = 0; destinationIndex < getNumThreads(); destinationIndex++) {
                    double weight = inputScanner.nextDouble();
                    // only process the right side of the matrix
                    if(destinationIndex < sourceIndex)
                        continue;
                    if(destinationIndex == sourceIndex)
                        continue;
                    if (weight != -1) {
                        Link link = new Link(threadIds[destinationIndex],threadIds[sourceIndex], weight);
                        threads.get(threadIds[sourceIndex]).addLink(link);
                        threads.get(threadIds[destinationIndex])
                                .addLink(Link.GetReverseLink(link));

                    }
                }
                if(sourceIndex != threads.size()-1)
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

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public String[] getThreadIds() {
		return threadIds;
	}

	public void setThreadIdMap(String[] threadIds) {
		this.threadIds = threadIds;
	}

}
