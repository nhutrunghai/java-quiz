import java.time.Instant;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

/**
 * Kết nối và thao tác collection lịch sử chơi (records).
 */
public class HistoryConnection {
	private final MongoCollection<Document> collection;

	public HistoryConnection() {
		// Tùy chỉnh URI/DB nếu cần
		MongoClient client = MongoClients
				.create("mongodb+srv://nhuthifc_db_user:30122005@netc.smhimsa.mongodb.net/");
		MongoDatabase database = client.getDatabase("quiz");
		this.collection = database.getCollection("records");
	}

	/**
	 * Thêm bản ghi lịch sử chơi.
	 */
	public void addHistory(String userId, Instant startTime, Instant endTime, int finalScore, int highestLevel,
			String gameStatus, boolean isSafeWin) {
		Document doc = new Document("user_id", userId).append("start_time", Date.from(startTime))
				.append("end_time", Date.from(endTime)).append("final_score", finalScore)
				.append("highest_level", highestLevel).append("game_status", gameStatus)
				.append("is_safe_win", isSafeWin);
		collection.insertOne(doc);
		System.out.println("Đã lưu lịch sử chơi cho user_id: " + userId);
	}

	/**
	 * Lấy danh sách lịch sử theo user_id, sắp xếp end_time giảm dần, giới hạn limit.
	 */
	public Iterable<Document> findHistoryByUser(String userId, int limit) {
		if (ObjectId.isValid(userId)) {
			return collection.find(Filters.or(Filters.eq("user_id", userId), Filters.eq("user_id", new ObjectId(userId))))
					.sort(Sorts.descending("end_time")).limit(limit);
		}
		return collection.find(Filters.eq("user_id", userId)).sort(Sorts.descending("end_time")).limit(limit);
	}
}
