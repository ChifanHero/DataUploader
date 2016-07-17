package app.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import app.bean.converter.MongoDocumentConverter;
import app.logger.StatusLogger;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is for saving data to mongodb.
 */
public class SaveDataTask implements Task<Object>{
	
	private MongoCollection<Document> collection;
	private StatusLogger statusLogger = StatusLogger.getInstance();
	
	private String database;

	public SaveDataTask(String database) {
		this.database = database;
	}
	
	public String getUniqueTaskId() {
		return this.getClass().getName();
	} 

	/**
	 * Expected dependency: </br>
	 * 1. restaurants to save.
	 */
	@SuppressWarnings("unchecked")
	public Object execute(List<Object> dependencies) {
		System.out.println("Starting SaveDataTask");
		if (dependencies == null || dependencies.size() != 1) {
			return Collections.emptyList();
		}
		collection = MongoClientFactory.getClient().getDatabase(database).getCollection("Restaurant");
		List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
		upsertData(restaurants);
		return null;
	}

	private void upsertData(List<Map<String, Object>> restaurants) {
		if (restaurants == null || restaurants.isEmpty()) {
			return;
		}
		int saved = 0;
		List<WriteModel<Document>> updates = new ArrayList<WriteModel<Document>>();
		List<Document> documents = new ArrayList<Document>();
		for (Map<String, Object> obj : restaurants) {
			documents.add(MongoDocumentConverter.convertOrCreate(obj));
		}
		for (Document document : documents) {
			UpdateOneModel<Document> model = new UpdateOneModel<Document>(
	                new Document("_id", document.get("_id")),                   // find part
	                new Document("$set",document),           // update part
	                new UpdateOptions().upsert(true)  // options like upsert
	        );
			updates.add(model);
		}
		try {
			collection.bulkWrite(updates);
			saved = updates.size();
		} catch (Exception e) {
			statusLogger.mongoLogger.setSuccess(false);
			statusLogger.mongoLogger.addError(e.getMessage());
			saved = 0;
		}
		statusLogger.summaryLogger.logTotalSaved(saved);
	}

	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
