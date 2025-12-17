import java.util.Scanner;

import org.bson.Document;

public class Login {
	Scanner scanner = new Scanner(System.in);
	Connection userService = new Connection();

	public Document welcome() {
		while (true)// Chạy cho đến khi break (chọn 1)
		{
			System.out.println("\n===== CHÀO MỪNG TRỞ LẠI =====");
			System.out.println("1. Đăng nhập");
			System.out.println("2. Đăng ký");
			System.out.print("Chọn chức năng (1-2): ");
			// Đọc số và xóa bộ đệm dòng để tránh trôi lệnh
			int check = Integer.parseInt(scanner.nextLine());

			if (check == 1) {
				// Sau khi thoát vòng lặp này, bạn sẽ gọi hàm login() ở bên dưới
				return sign_in();
			}
			if (check == 2) {
				System.out.print("Nhập username: ");
				String user = scanner.nextLine();
				System.out.print("Nhập email: ");
				String email = scanner.nextLine();
				System.out.print("Nhập password: ");
				String pass = scanner.nextLine();

				// Gọi hàm addUser đã có logic kiểm tra trùng username
				userService.sign_up(user, email, pass);
			}
		}
	}

	public Document sign_in() {
		while (true) {
			Document loggedInUser = null;
			while (loggedInUser == null) {
				System.out.println("--- ĐĂNG NHẬP HỆ THỐNG ---");
				System.out.print("Username: ");
				String user = scanner.nextLine();
				System.out.print("Password: ");
				String pass = scanner.nextLine();

				loggedInUser = userService.login(user, pass);
				if (loggedInUser != null) {
					System.out.println("Quyền hạn của bạn: " + loggedInUser.getString("role"));
					return loggedInUser;
				}

			}
		}
	}

}
