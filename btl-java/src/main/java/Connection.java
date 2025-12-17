import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

public class Connection {
	private MongoCollection<Document> collection;

	public Connection() {
		// Kết nối tới MongoDB (Thay đổi URI nếu cần)
		MongoClient mongoClient = MongoClients
				.create("mongodb+srv://nhuthifc_db_user:30122005@netc.smhimsa.mongodb.net/");
		MongoDatabase database = mongoClient.getDatabase("quiz");
		this.collection = database.getCollection("users");
	}

	// --- 1. THÊM NGƯỜI DÙNG (CREATE) ---
	public void addUser(String username, String email, String password, double money, String role) {
		// 1. Kiểm tra xem username đã tồn tại trong Database chưa
		Document existingUser = collection.find(eq("username", username)).first();

		if (existingUser != null) {
			System.out
					.println("❌ LỖI: Tên người dùng [" + username + "] đã có trong hệ thống, vui lòng nhập tên khác!");
			return; // Dừng lại, không thực hiện insert
		}

		// 2. Nếu chưa có thì mới tạo Document mới
		Document newUser = new Document("username", username).append("email", email).append("password", password)
				.append("money", money).append("role", role).append("is_active", true);

		// 3. Chèn vào MongoDB
		collection.insertOne(newUser);
		System.out.println("✅ Thêm thành công người dùng: " + username);
	}

	public void sign_up(String username, String email, String password) {
		// 1. Kiểm tra xem username đã tồn tại trong Database chưa
		Document existingUser = collection.find(eq("username", username)).first();

		if (existingUser != null) {
			System.out
					.println("❌ LỖI: Tên người dùng [" + username + "] đã có trong hệ thống, vui lòng nhập tên khác!");
			return; // Dừng lại, không thực hiện insert
		}

		// default information
		Double money = 1000.0;
		String role = "user";

		// 2. Nếu chưa có thì mới tạo Document mới
		Document newUser = new Document("username", username).append("email", email).append("password", password)
				.append("money", money).append("role", role).append("is_active", true);
		;

		// 3. Chèn vào MongoDB
		collection.insertOne(newUser);
		System.out.println("✅ Đăng ký thành công người dùng: " + username);
		System.out.println("Vui lòng đăng nhập lại");
	}

	// --- 2. HIỂN THỊ THÔNG TIN (READ) ---
	public void displayAllUsers() {
		System.out.println(String.format("%-15s | %-20s | %-15s| %-15s | %-10s | %-10s", "Username", "Email",
				"Password", "Money", "Role", "Active"));
		System.out.println("----------------------------------------------------------------------------------");

		for (Document doc : collection.find()) {
			// CÁCH SỬA LỖI: Lấy thông qua kiểu Number
			Object moneyObj = doc.get("money");
			double money = 0.0;

			if (moneyObj instanceof Number) {
				money = ((Number) moneyObj).doubleValue();
			}

			System.out.println(String.format("%-15s | %-20s | %-15s | %-15.2f | %-10s | %-10s",
					doc.getString("username"), doc.getString("email"), // Sử dụng biến money đã ép kiểu an toàn
					doc.getString("password"), money, doc.getString("role"), doc.get("is_active") // Dùng get() chung
																									// cho an
			// toàn hoặc getBoolean()
			));
		}
	}

	// --- 3. SỬA THÔNG TIN (UPDATE) ---
	public void updateFullUserInfo(String targetUsername, String newUsername, String newEmail, String newPass,
			double newMoney, String newRole, boolean newStatus) {
		Bson filter = eq("username", targetUsername);

		// Gom tất cả các thay đổi lại
		Bson updates = Updates.combine(Updates.set("username", newUsername), Updates.set("email", newEmail),
				Updates.set("password", newPass), Updates.set("money", newMoney), Updates.set("role", newRole),
				Updates.set("is_active", newStatus));

		long modifiedCount = collection.updateOne(filter, updates).getModifiedCount();

		if (modifiedCount > 0) {
			System.out.println("✅ Đã cập nhật toàn bộ thông tin cho người dùng: " + targetUsername);
		} else {
			System.out.println("❓ Không có thay đổi nào được thực hiện hoặc không tìm thấy người dùng.");
		}
	}

	// --- 4. XÓA NGƯỜI DÙNG (DELETE) ---
	public void deleteUser(String username) {
		collection.deleteOne(eq("username", username));
		System.out.println("Đã xóa người dùng: " + username);
	}

	// đăng nhập
	public Document login(String username, String password) {
		// Tìm bản ghi thỏa mãn đồng thời cả username và password
		// Lọc thêm điều kiện is_active = true để đảm bảo tài khoản không bị khóa
		Document user = collection.find(and(eq("username", username), eq("password", password), eq("is_active", true)))
				.first();

		if ((user != null)) {
			System.out.println("=> Đăng nhập thành công! Chào mừng " + username);
			return user;
		} else {
			System.out.println("=> Đăng nhập thất bại: Sai tài khoản, mật khẩu hoặc tài khoản bị khóa.");
			return null;
		}
	}
}