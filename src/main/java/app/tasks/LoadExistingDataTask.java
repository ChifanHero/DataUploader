package app.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import app.logger.StatusLogger;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is for loading existing data from mongodb.
 */
public class LoadExistingDataTask implements Task<List<Map<String, Object>>>{

	private StatusLogger statusLogger = StatusLogger.getInstance(); 
	
	private String database;
	
	public LoadExistingDataTask(String database) {
		this.database = database;
	}
	
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	/**
	 * This task does not have any dependency.
	 */
	public List<Map<String, Object>> execute(List<Object> dependencies) {
		MongoCollection<Document> collection = MongoClientFactory.getClient().getDatabase(database).getCollection("Restaurant");
		if (collection.count() <= 0) {
			return Collections.emptyList();
		}
		FindIterable<Document> iterable = collection.find();
		final List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		iterable.forEach(new Block<Document>(){
			public void apply(Document arg0) {
				results.add(convert(arg0));
			}
			
		});
		statusLogger.summaryLogger.logTotalRecordsFromDB(results.size());
		return results;
	}

//	{
//	    "_id": "04b5e7kIps",
//	    "_created_at": {
//	        "$date": "2015-12-28T04:26:13.491Z"
//	    },
//	    "address": "18438 Colima Rd, #102, Rowland Heights, CA 91748",
//	    "county": "LA",
//	    "gid": "20922",
//	    "english_name": "Three Family's Village",
//	    "phone": "(626) 810-4993",
//	    "_updated_at": {
//	        "$date": "2015-12-28T04:43:56.891Z"
//	    },
//	    "name": "三家村",
//	    "coordinates": [
//	        -117.902116,
//	        33.986473
//	    ]
//	}
	protected Map<String, Object> convert(Document arg0) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (arg0 != null) {
			for (Entry<String, Object> entry : arg0.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				result.put(key, value);
			}
		}
		return result;
	}

	public void failedToComplete() {
		System.err.println("Not able to load mongodb data within 5 seconds. Abort data upload.");
		System.exit(0);
	}

	public long getTimeout() {
		return 5000;
	}

}
