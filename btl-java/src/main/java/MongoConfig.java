import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Simple MongoDB connector with overridable URI and database name.
 */
public class MongoConfig {
	private static final String DEFAULT_URI = "mongodb+srv://nhuthifc_db_user:30122005@netc.smhimsa.mongodb.net/";
	private static final String DEFAULT_DB = "quiz";
	private static final String USERS_COLLECTION = "users";

	private final MongoClient client;
	private final MongoDatabase database;

	public MongoConfig() {
		this(null, null);
	}

	public MongoConfig(String uri, String dbName) {
		String mongoUri = (uri != null && !uri.isEmpty()) ? uri : getEnvOrDefault("MONGO_URI", DEFAULT_URI);
		String databaseName = (dbName != null && !dbName.isEmpty()) ? dbName : getEnvOrDefault("MONGO_DB",
				DEFAULT_DB);

		this.client = MongoClients.create(mongoUri);
		this.database = client.getDatabase(databaseName);
	}

	private String getEnvOrDefault(String key, String fallback) {
		String value = System.getenv(key);
		return (value != null && !value.isEmpty()) ? value : fallback;
	}

	public MongoCollection<Document> usersCollection() {
		return database.getCollection(USERS_COLLECTION);
	}

	public MongoDatabase getDatabase() {
		return database;
	}

	public MongoClient getClient() {
		return client;
	}

	public void close() {
		client.close();
	}
}
