package app.bean.converter;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import data.mongodb.IdGenerator;

public class MongoDocumentConverter {
	
	public static Document convertOrCreate(Map<String, Object> object) {
		if (object == null) {
			return null;
		}
		Date now = new Date();
		Document document = new Document("_updated_at", now);
		if (object.get("_id") == null) {
			String id = IdGenerator.getNewObjectId();
			document.append("_id", id);
		} 
		if (object.get("_created_at") == null) {
			document.put("_created_at", now);
		}
		for (Entry<String, Object> entry : object.entrySet()) {
			document.put(entry.getKey(), entry.getValue());
		}
		return document;
	}
	

}
