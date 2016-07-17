package data.mongodb;

import com.mongodb.MongoClient;

public class MongoClientFactory {
	
	private static MongoClient client;
		
	public static MongoClient getClient() {
		return client;
	}
	
	public static void setClient(MongoClient client) {
		MongoClientFactory.client = client;
	}

}
