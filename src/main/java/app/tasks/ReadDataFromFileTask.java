package app.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.csv.SuperCSVReader;
import app.logger.StatusLogger;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is for reading data from files.
 */
public class ReadDataFromFileTask implements Task<List<Map<String, Object>>>{
	
	private List<String> filePaths;
	private StatusLogger statusLogger = StatusLogger.getInstance();
	
	public ReadDataFromFileTask(List<String> files) {
		if (files == null) {
			throw new IllegalArgumentException("file paths must be provided");
		}
		this.filePaths = files;
	}

	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	public List<Map<String, Object>> execute(List<Object> dependencies) {
		System.out.println("Starting ReadDataFromFileTask");
		if (filePaths == null || filePaths.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (String file : filePaths) {
			List<Map<String, Object>> subResults = new ArrayList<Map<String, Object>>();
//			BufferedReader br;
			try {
				subResults.addAll(SuperCSVReader.read(file));
//				br = new BufferedReader(new FileReader(file));
//				String headerLine = br.readLine();
//				if (headerLine == null) {
//					continue;
//				}
//				MapCSVParser csvParser = new MapCSVParser(headerLine);
//				String content = null;
//				while((content=br.readLine())!=null){
//					Map<String, Object> result = csvParser.parseLine(content);
//					subResults.add(result);
//			    }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.err.println("Not able to find file. Abort data upload.");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Not able to read data. Abort data upload.");
				System.exit(0);
			}
			statusLogger.summaryLogger.logRowsForFile(file, subResults.size());
			results.addAll(subResults);
			
		}
		System.out.println("End read file task");
		statusLogger.summaryLogger.logTotalRows(results.size());
		return results;
		
	}

	public void failedToComplete() {
		System.err.println("Not able to read data within 10s. Abort");
		System.exit(0);
	}

	public long getTimeout() {
		// TODO Auto-generated method stub
		return 10000;
	}

}
