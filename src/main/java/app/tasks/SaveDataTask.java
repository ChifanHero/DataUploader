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
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is for saving data to mongodb.
 */
public class SaveDataTask implements Task<Object>{
	
	private MongoCollection<Document> collection;

	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	/**
	 * Expected dependency: </br>
	 * 1. restaurants to save.
	 */
	@SuppressWarnings("unchecked")
	public Object execute(List<Object> dependencies) {
		if (dependencies == null || dependencies.size() != 1) {
			return Collections.emptyList();
		}
		collection = MongoClientFactory.getClient().getDatabase("lightning-staging").getCollection("Restaurant");
		List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
		upsertData(restaurants);
		return null;
	}

	private void upsertData(List<Map<String, Object>> restaurants) {
		if (restaurants == null || restaurants.isEmpty()) {
			return;
		}
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
		collection.bulkWrite(updates);
	}

//	private void updateData(List<Map<String, Object>> toUpdate) {
//		
//	}
//
//	private void insertData(List<Map<String, Object>> toInsert) {
//		if (toInsert == null || toInsert.isEmpty()) {
//			return;
//		}
//		List<Document> documents = new ArrayList<Document>();
//		for (Map<String, Object> newObj : toInsert) {
//			Document newDoc = MongoDocumentConverter.createNewDocument(newObj);
//			documents.add(newDoc);
//		}
//		try {
//			collection.insertMany(documents);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

	
//	private void separateInsertAndUpdate(List<Map<String, Object>> restaurants, List<Map<String, Object>> inserts, List<Map<String, Object>> updates) {
//		if (restaurants == null || inserts == null || updates == null) {
//			return;
//		}
//		for (Map<String, Object> restaurant : restaurants) {
//			if (restaurant.get("_id") == null) {
//				inserts.add(restaurant);
//			} else {
//				updates.add(restaurant);
//			}
//		}
//	}

	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
