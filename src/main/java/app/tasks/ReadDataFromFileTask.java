package app.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.bean.BeanProperties;
import app.bean.formatter.RestaurantFormatter;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is for reading data from files.
 */
public class ReadDataFromFileTask implements Task<List<Map<String, Object>>>{
	
	private List<String> filePaths;
	
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
		if (filePaths == null || filePaths.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (String file : filePaths) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				String headerLine = br.readLine();
				if (headerLine == null) {
					continue;
				}
				Map<Integer, String> headers = getHeaders(headerLine);
				String content = null;
				while((content=br.readLine())!=null){
					Map<String, Object> result = new HashMap<String, Object>();
			        String str[] = content.split(",");
			        for(int i=0;i<str.length;i++){
			        	String propertyName = headers.get(i);
			        	String propertyValue = str[i];
			        	if (BeanProperties.NAME.equals(propertyName)) {
			        		propertyValue = RestaurantFormatter.formatChineseName(propertyValue);
			        	} else if (BeanProperties.PHONE.equals(propertyName)) {
			        		propertyValue = RestaurantFormatter.formatPhone(propertyValue);
			        	}
			        	
			        	result.put(propertyName, propertyValue);
			        }
			        results.add(result);
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
			
		}
		return results;
		
	}

	private Map<Integer, String> getHeaders(String headerLine) {
		String[] headers = headerLine.split(",");
		if (headers != null && headers.length > 0) {
			Map<Integer, String> result = new HashMap<Integer, String>();
			for (int i = 0; i < headers.length; i++) {
				result.put(i, headers[i]);
			}
			return result;
		} else {
			return Collections.emptyMap();
		}
	}

	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
