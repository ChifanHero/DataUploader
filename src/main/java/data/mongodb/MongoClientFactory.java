package data.mongodb;

import com.mongodb.MongoClient;

public class MongoClientFactory {
	
	private static ThreadLocal<MongoClient> store = new ThreadLocal<MongoClient>();
	
	public static MongoClient getClient() {
		return store.get();
	}
	
	public static void setClient(MongoClient client) {
		store.set(client);
	}

}
