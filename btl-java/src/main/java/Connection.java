import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

public class Connection {
	private MongoCollection<Document> collection;

	public Connection() {
		MongoClient mongoClient = MongoClients
				.create("mongodb+srv://nhutrunghai_db_user:SaQjyJC8xvwjpI20@cluster0.9rj7y8x.mongodb.net/");
		MongoDatabase database = mongoClient.getDatabase("quiz");
		this.collection = database.getCollection("users");
	}

	public boolean checkUser(String username) {
		Document existingUser = collection.find(eq("username", username)).first();
		if (existingUser != null) {
			System.out.println("❌ Loi: Ten nguoi dung [" + username + "] da co trong he thong, vui long nhap ten khac!");
			return false;
		}
		return true;
	}

	public boolean checkEmail(String email) {
		Document existingEmail = collection.find(eq("email", email)).first();
		if (existingEmail != null) {
			System.out.println("❌ Loi: Email [" + email + "] da co trong he thong, vui long nhap email khac!");
			return false;
		}
		return true;
	}

	public void addUser(String username, String email, String password, double money, String role) {
		Document existingUser = collection.find(eq("username", username)).first();

		if (existingUser != null) {
			System.out.println("❌ Loi: Ten nguoi dung [" + username + "] da co trong he thong, vui long nhap ten khac!");
			return;
		}

		Document newUser = new Document("username", username).append("email", email).append("password", password)
				.append("money", money).append("role", role).append("is_active", true);

		collection.insertOne(newUser);
		System.out.println("✅ Them thanh cong nguoi dung: " + username);
	}

	public void sign_up(String username, String email, String password) {
		Document existingUser = collection.find(eq("username", username)).first();

		if (existingUser != null) {
			System.out.println("❌ Loi: Ten nguoi dung [" + username + "] da co trong he thong, vui long nhap ten khac!");
			return;
		}

		Double money = 1000.0;
		String role = "user";

		Document newUser = new Document("username", username).append("email", email).append("password", password)
				.append("money", money).append("role", role).append("is_active", true);

		collection.insertOne(newUser);
		System.out.println("✅ Dang ky thanh cong nguoi dung: " + username);
		System.out.println("ℹ️ Vui long dang nhap lai");
	}

	public void displayAllUsers() {
		System.out.println("📋 Danh sach nguoi dung:");
		System.out.println(String.format("%-15s | %-20s | %-15s| %-15s | %-10s | %-10s", "Username", "Email",
				"Password", "Money", "Role", "Active"));
		System.out.println("----------------------------------------------------------------------------------");

		for (Document doc : collection.find()) {
			Object moneyObj = doc.get("money");
			double money = 0.0;

			if (moneyObj instanceof Number) {
				money = ((Number) moneyObj).doubleValue();
			}

			System.out.println(String.format("%-15s | %-20s | %-15s | %-15.2f | %-10s | %-10s",
					doc.getString("username"), doc.getString("email"), doc.getString("password"), money,
					doc.getString("role"), doc.get("is_active")));
		}
	}

	public void updateFullUserInfo(String targetUsername, String newUsername, String newEmail, String newPass,
			double newMoney, String newRole, boolean newStatus) {
		Bson filter = eq("username", targetUsername);

		Bson updates = Updates.combine(Updates.set("username", newUsername), Updates.set("email", newEmail),
				Updates.set("password", newPass), Updates.set("money", newMoney), Updates.set("role", newRole),
				Updates.set("is_active", newStatus));

		long modifiedCount = collection.updateOne(filter, updates).getModifiedCount();

		if (modifiedCount > 0) {
			System.out.println("✅ Da cap nhat toan bo thong tin cho nguoi dung: " + targetUsername);
		} else {
			System.out.println("❓ Khong co thay doi nao duoc thuc hien hoac khong tim thay nguoi dung.");
		}
	}

	public void updateMoney(String username, double amountToAdd) {
		if (amountToAdd == 0) {
			System.out.println("⚠️ Khong co so tien can cap nhat.");
			return;
		}

		Bson filter = eq("username", username);
		Bson update = Updates.inc("money", amountToAdd);

		long matchedCount = collection.updateOne(filter, update).getMatchedCount();

		if (matchedCount > 0) {
			System.out.println("✅ Da cap nhat so tien cho nguoi dung: " + username);
		} else {
			System.out.println("❓ Khong co thay doi nao duoc thuc hien hoac khong tim thay nguoi dung.");
		}
	}

	public void deleteUser(String username) {
		collection.deleteOne(eq("username", username));
		System.out.println("🗑️ Da xoa nguoi dung: " + username);
	}

	public Document login(String username, String password) {
		Document user = collection.find(and(eq("username", username), eq("password", password), eq("is_active", true)))
				.first();

		if ((user != null)) {
			return user;
		} else {
			System.out.println("❌ Dang nhap that bai: Sai tai khoan, mat khau hoac tai khoan bi khoa.");
			return null;
		}
	}

	public Document findUserById(String userId) {
		if (userId == null || userId.trim().isEmpty()) {
			return null;
		}

		if (ObjectId.isValid(userId)) {
			Document user = collection.find(eq("_id", new ObjectId(userId))).first();
			if (user != null) {
				return user;
			}
		}

		return collection.find(eq("_id", userId)).first();
	}

	public void displayLeaderboard() {
		System.out.println("\n==========🏆 BANG XEP HANG NGUOI CHOI CAO NHAT ==========");
		System.out.printf("%-5s | %-15s | %-15s%n", "STT", "Ten nguoi choi", "So tien (VND)");
		System.out.println("-------------------------------------------------------");

		int rank = 1;
		for (Document doc : collection.find().sort(Sorts.descending("money")).limit(10)) {
			String username = doc.getString("username");
			double money = 0;
			if (doc.get("money") instanceof Number) {
				money = ((Number) doc.get("money")).doubleValue();
			}

			System.out.printf("%-5d | %-15s | %-15.0f%n", rank++, username, money);
		}
		System.out.println("=======================================================");
	}
}
