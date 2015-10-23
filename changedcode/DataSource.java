import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;

/*
 * created by : Prashant Prakash
 * created date : 17th oct 2015 
 * Description : To read input data from different data sources
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

	public void readWeights(String inputpath, Map<String, SyncGHSThread> threads) {
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
                    if (weight != Constant.NEGONE) {
                        Link link = new Link(threadIds[destinationIndex], weight);
                        threads.get(threadIds[sourceIndex]).addLink(link);
                        threads.get(threadIds[destinationIndex])
                                .addLink(Link.GetReverseLink(link, threadIds[sourceIndex]));

                    }
                }
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
