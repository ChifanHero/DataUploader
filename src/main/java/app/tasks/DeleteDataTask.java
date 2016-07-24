package app.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOneModel;

import app.logger.StatusLogger;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is for deleting data from mongodb.
 */
public class DeleteDataTask implements Task<Object>{
	
	private MongoCollection<Document> collection;
	private StatusLogger statusLogger = StatusLogger.getInstance();
	
	private String database;

	public DeleteDataTask(String database) {
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
		System.out.println("Starting DeleteDataTask");
		if (dependencies == null || dependencies.size() != 1) {
			return Collections.emptyList();
		}
		collection = MongoClientFactory.getClient().getDatabase(database).getCollection("Restaurant");
		List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
		System.out.println(restaurants.size() + " restaurants to be deleted");
		deleteData(restaurants);
		System.out.println("Successfully deleted data");
		return null;
	}
	
	private void deleteData(List<Map<String, Object>> restaurants) {
		if (restaurants == null || restaurants.isEmpty()) {
			return;
		}
		List<DeleteOneModel<Document>> deletes = new ArrayList<DeleteOneModel<Document>>();
		for (Map<String, Object> obj : restaurants) {
			DeleteOneModel<Document> model = new DeleteOneModel<Document>(
	                new Document("_id", obj.get("_id")));
			deletes.add(model);	
		}
		try {
			collection.bulkWrite(deletes);
			System.out.println("Successfully deleted " + deletes.size() + " documents");
		} catch (Exception e) {
			e.printStackTrace();
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
