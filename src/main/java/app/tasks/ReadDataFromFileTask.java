package app.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.csv.MapCSVParser;
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
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				String headerLine = br.readLine();
				if (headerLine == null) {
					continue;
				}
				MapCSVParser csvParser = new MapCSVParser(headerLine);
				String content = null;
				while((content=br.readLine())!=null){
					Map<String, Object> result = csvParser.parseLine(content);
					subResults.add(result);
			    }
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
		statusLogger.summaryLogger.logTotalRows(results.size());
		return results;
		
	}

	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
