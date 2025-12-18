import java.util.Scanner;
import org.bson.Document;

public class Login {
	Scanner scanner = new Scanner(System.in);
	Connection userService = new Connection();

	public Document welcome() {
		while (true)// Chạy cho đến khi break (chọn 1)
		{
			System.out.println("\n===== 👋 CHÀO MỪNG TRỞ LẠI =====");
			System.out.println("1. 🔑 Đăng nhập");
			System.out.println("2. 📝 Đăng ký");
			System.out.print("👉 Chọn chức năng (1-2): ");
			// Đọc số và xóa bộ đệm dòng để tránh trôi lệnh
			int check = -1;;
			try {
				check = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("❌ Vui lòng nhập số!");
			}

			if (check == 1) {
				// Sau khi thoát vòng lặp này, bạn sẽ gọi hàm login() ở bên dưới
				return sign_in();
			}
			if (check == 2) {
				String user = promptNonEmpty("👤 Nhập username: ");
				String email = promptNonEmpty("📧 Nhập email: ");
				String pass = promptNonEmpty("🔒 Nhập password: ");

				// Gọi hàm addUser đã có logic kiểm tra trùng username
				userService.sign_up(user, email, pass);
			}
		}
	}

	public Document sign_in() {
		while (true) {
			Document loggedInUser = null;
			while (loggedInUser == null) {
				System.out.println("--- 🔐 ĐĂNG NHẬP HỆ THỐNG ---");
				String user = promptNonEmpty("👤 Username: ");
				String pass = promptNonEmpty("🔒 Password: ");

				loggedInUser = userService.login(user, pass);
				if (loggedInUser != null) {
					System.out.println("🛡️ Quyền hạn của bạn: " + loggedInUser.getString("role"));
					return loggedInUser;
				}

			}
		}
	}

	private String promptNonEmpty(String message) {
		while (true) {
			System.out.print(message);
			String input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				return input;
			}
			System.out.println("⚠️ Trường này không được bỏ trống, vui lòng nhập lại.");
		}
	}
}
