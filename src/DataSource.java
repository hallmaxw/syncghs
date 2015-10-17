import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

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
			String line = br.readLine();
			int i = 0;
			while (line != null) {
				if (i == 0) {
					numThreads = Integer.parseInt(line.trim());
				} else if (i == 1) {
					threadIds = line.split(Constant.SPACESEP);
				}
				line = br.readLine();
				i++;
			}

		} catch (Exception ex) {
			System.err.println("Error in reading input File");
		}
	}

	public void readWeights(String inputpath, Map<String, SyncGHSThread> threads) {
		try {
			FileReader input = new FileReader(inputpath);
			BufferedReader br = new BufferedReader(input);
			String line = br.readLine();
			int i = 0;
			while (line != null) {
				if (i > 1) {
					String[] lineArr = line.split(Constant.TABSEP);
					for (int index = 0; index < lineArr.length; index++) {
						if (Double.parseDouble(lineArr[index]) != Constant.NEGONE) {
							Link link = new Link(threadIds[index], Double.parseDouble(lineArr[index]));
							threads.get(threadIds[i - 2]).addLink(link);
							threads.get(threadIds[index]).addLink(
									Link.GetReverseLink(link, threadIds[i - 2], Double.parseDouble(lineArr[index])));

						}
					}
				}
				line = br.readLine();
				i++;
			}

		} catch (Exception ex) {
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
