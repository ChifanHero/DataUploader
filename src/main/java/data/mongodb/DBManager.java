package data.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DBManager {
	
	private String URI;
	
	public DBManager(String URI) {
		this.URI = URI;
	}
	
	public MongoClient createClient() {
		MongoClientURI connectionString = new MongoClientURI(URI);
		MongoClient mongoClient = new MongoClient(connectionString);
		return mongoClient;
	}

}
