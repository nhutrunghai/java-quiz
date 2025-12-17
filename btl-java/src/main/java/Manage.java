import java.util.Scanner;

public class Manage {

	Connection userService = new Connection();
	Scanner scanner = new Scanner(System.in);

	public void ask_manager() {
		while (true) {
			System.out.println("\n===== HỆ THỐNG QUẢN LÝ  =====");
			System.out.println("1. Truy cập vào quản lý");
			System.out.println("2. Chơi");
			System.out.print("Chọn chức năng (1-2): ");
			int check = Integer.parseInt(scanner.nextLine());
			if (check == 1) {
				manage_user();
				break;
			} else {
				break;
			}
		}
	}

	public void manage_user() {
		while (true) {
			System.out.println("\n===== HỆ THỐNG QUẢN LÝ NGƯỜI DÙNG =====");
			System.out.println("1. Hiển thị danh sách người dùng");
			System.out.println("2. Thêm người dùng mới");
			System.out.println("3. Sửa thông tin người dùng");
			System.out.println("4. Xóa người dùng");
			System.out.println("0. Thoát chương trình");
			System.out.print("Chọn chức năng (0-4): ");

			int selected;
			// Kiểm tra nếu người dùng nhập không phải là số
			if (!scanner.hasNextInt()) {
				System.out.println("Vui lòng chỉ nhập số!");
				scanner.next();
				continue;
			}

			selected = scanner.nextInt();
			scanner.nextLine(); // Đọc bỏ dòng trống sau khi nhập số

			switch (selected) {
			case 1:
				userService.displayAllUsers();
				break;

			case 2:
				System.out.print("Nhập username: ");
				String user = scanner.nextLine();
				if (!userService.checkUser(user)) {
					break;
				}
				;
				System.out.print("Nhập email: ");
				String email = scanner.nextLine();
				System.out.print("Nhập password: ");
				String pass = scanner.nextLine();
//				System.out.print("Nhập số tiền: ");
//				double money = scanner.nextDouble();
//				scanner.nextLine();
				double money;
				while (true) {
					try {
						System.out.print("Nhập số tiền: ");
						String input = scanner.nextLine(); // Đọc dạng chuỗi trước
						money = Double.parseDouble(input); // Thử chuyển sang số

						if (money <= 0) {
							System.out.println("❌ Số tiền không được bằng 0 hoặc âm. Vui lòng nhập lại!");
							continue;
						}
						break;
					} catch (NumberFormatException e) {
						System.out.println("❌ Lỗi: Vui lòng nhập số hợp lệ (ví dụ: 100.5)!");
					}
				}
				String role;
				while (true) {
					System.out.print("Nhập vai trò (admin/user): ");
					role = scanner.nextLine();

					if ((!role.equals("admin") && (!role.equals("user")))) {
						System.out.println("❌ Lỗi: Vui lòng nhập vai trò hợp lệ (ví dụ: user)!");
						continue;
					}
					break;
				}

				userService.addUser(user, email, pass, money, role);
				break;

			case 3:
				System.out.print("Nhập username cần sửa thông tin: ");
				String uUpdate = scanner.nextLine();

				// Kiểm tra xem người dùng có tồn tại không trước khi bắt nhập đống thông tin
				// (Giả sử bạn có hàm findUser hoặc dùng collection.find trong Connection)

				System.out.println("--- Nhập thông tin mới ---");
				System.out.print("Username mới: ");
				String nUsername = scanner.nextLine();
				System.out.print("Email mới: ");
				String nEmail = scanner.nextLine();

				System.out.print("Mật khẩu mới: ");
				String nPass = scanner.nextLine();

				double nMoney = 0;
				while (true) {
					try {
						System.out.print("Số tiền mới: ");
						nMoney = Double.parseDouble(scanner.nextLine());
						break;
					} catch (Exception e) {
						System.out.println("❌ Vui lòng nhập số tiền hợp lệ!");
					}
				}

				String nRole;
				while (true) {
					System.out.print("Vai trò mới (ADMIN/USER): ");
					nRole = scanner.nextLine();

					if ((!nRole.equals("admin") && (!nRole.equals("user")))) {
						System.out.println("❌ Lỗi: Vui lòng nhập vai trò hợp lệ (ví dụ: user)!");
						continue;
					}
					break;
				}

				System.out.print("Trạng thái hoạt động (true/false): ");
				boolean nStatus;
				while (true) {
					nStatus = Boolean.parseBoolean(scanner.nextLine());
					if ((nStatus != true) && (nStatus != false)) {
						System.out.println("❌ Lỗi: Vui lòng nhập trạng thái hoạt động hợp lệ (ví dụ: true)!");
						continue;
					}
					break;
				}

				// Gọi hàm cập nhật tổng thể
				userService.updateFullUserInfo(uUpdate, nUsername, nEmail, nPass, nMoney, nRole, nStatus);
				break;

			case 4:
				System.out.print("Nhập username cần xóa: ");
				String uDelete = scanner.nextLine();
				userService.deleteUser(uDelete);
				break;

			case 0:
				System.out.println("Đang thoát...");
				return;
			default:
				System.out.println("Lựa chọn không hợp lệ!");
			}
		}
	}
}
