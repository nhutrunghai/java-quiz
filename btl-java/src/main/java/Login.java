import java.util.Scanner;
import org.bson.Document;
import org.apache.commons.validator.routines.EmailValidator;
public class Login {
	Scanner scanner = new Scanner(System.in);
	Connection userService = new Connection();

	public Document welcome() {
		while (true)
		{
			System.out.println("\n===== 👋 CHÀO MỪNG TRỞ LẠI =====");
			System.out.println("1. 🔑 Đăng nhập");
			System.out.println("2. 📝 Đăng ký");
			System.out.print("👉 Chọn chức năng (1-2): ");
			
			int check = -1;;
			try {
				check = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("❌ Vui lòng nhập số!");
			}

			if (check == 1) {
				
				return sign_in();
			}
			if (check == 2) {
				String user = promptNonEmpty("👤 Nhập username: ",false);
				String email = promptNonEmpty("📧 Nhập email: ",true);
				String pass = promptNonEmpty("🔒 Nhập password: ",false);

				
				userService.sign_up(user, email, pass);
			}
		}
	}

	public Document sign_in() {
		while (true) {
			Document loggedInUser = null;
			while (loggedInUser == null) {
				System.out.println("--- 🔐 ĐĂNG NHẬP HỆ THỐNG ---");
				String user = promptNonEmpty("👤 Username: ",false);
				String pass = promptNonEmpty("🔒 Password: ",false);

				loggedInUser = userService.login(user, pass);
				if (loggedInUser != null) {
					System.out.println("🛡️ Quyền hạn của bạn: " + loggedInUser.getString("role"));
					return loggedInUser;
				}

			}
		}
	}

	private String promptNonEmpty(String message,Boolean isemail ) {
		String input;
		while (true) {
			System.out.print(message);
			input = scanner.nextLine().trim();
			if (input.isEmpty()) {
				System.out.println("⚠️ Trường này không được bỏ trống, vui lòng nhập lại.");
				continue;
			}
			if(!isemail){
				return input;
			}
			boolean valid = EmailValidator.getInstance().isValid(input);
			if(valid){
				return input;
			}
			System.out.println("📧 Không đúng định dạng email ."); 
		}
	}
}
