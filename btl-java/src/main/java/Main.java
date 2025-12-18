import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

public class Main {
	static Scanner sc = new Scanner(System.in, "UTF-8");

	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String YELLOW = "\u001B[33m";
	public static final String GREEN = "\u001B[32m";

	public static void main(String[] args) {
		Connection userService = new Connection();
		Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

		Document loggedInUser = loginLoop(userService);
		boolean isAdmin = "admin".equalsIgnoreCase(loggedInUser.getString("role"));

		boolean exit = false;
		while (!exit) {
			int choice = showMainMenu(sc, isAdmin);
			switch (choice) {
			case 1:
				// Bắt đầu trò chơi
				HandleQuestion.Result result = HandleQuestion.play(sc);
				
				if (result != null) {
					String currentUserID = loggedInUser.getObjectId("_id").toHexString();
					String currentUsername = loggedInUser.getString("username");
					// 1. Ghi vào lịch sử chơi
					HandleHistory.writeHistory(currentUserID,result.getStartTime(), result.getEndTime(), result.getMoney(),
							result.getCorrectCount() + 1, result.getStatus(), result.getIsSafeWin());
					
					// 2. Cập nhật tiền vào MongoDB
					// Lấy số tiền hiện tại của User (ép kiểu double an toàn)
					if(result.getMoney() > 0){
						// Cong them vao so du hien tai thay vi set lai toan bo
						userService.updateMoney(currentUsername, result.getMoney());

						// Cap nhat lai so du trong doi tuong dang dang nhap cho lan choi sau
						Object moneyObj = loggedInUser.get("money");
						double oldMoney = 0;
						if (moneyObj instanceof Double) oldMoney = (Double) moneyObj;
						else if (moneyObj instanceof Integer) oldMoney = ((Integer) moneyObj).doubleValue();
						loggedInUser.put("money", oldMoney + result.getMoney());
					}

					System.out.println(YELLOW + "💰 Số tiền nhận được : " + result.getMoney() +  RESET);
					
				}
				break;
			case 2:
				handleGetInfo.showInfo(loggedInUser.getObjectId("_id").toHexString());
				break;
			case 3:
				System.out.println(
						"\n=====📜 LICH SU CHOI CUA " + loggedInUser.getString("username").toUpperCase() + " =====");
				HandleHistory.showHistory(loggedInUser.getObjectId("_id").toHexString(),
						loggedInUser.getString("username"));
				break;
			case 4:
				System.out.println(
						"\n=====📜 THONG KE NGUOI CHOI " + loggedInUser.getString("username").toUpperCase() + " =====");
				HandlePlayer.showPlayerStats(loggedInUser.getObjectId("_id").toHexString(),
						loggedInUser.getString("username"));
				break;
			case 5:
				userService.displayLeaderboard(loggedInUser.getString("username"));
				break;
			case 6:
				Manage admin = new Manage();
				admin.manage_user();
				break;
			case 7:
				QuestionService questionService = new QuestionService();
				ManageQuestions qManager = new ManageQuestions(questionService, sc);
				qManager.showMenu();
				break;
			case 0:
				System.out.println("Dang xuat...");	
				loggedInUser = loginLoop(userService);
				isAdmin = "admin".equalsIgnoreCase(loggedInUser.getString("role"));
				break;
			default:
				break;
			}
		}
	}

	private static Document loginLoop(Connection userService) {
		Document loggedInUser = null;
		while (loggedInUser == null) {
			Login login = new Login();

			loggedInUser = login.welcome();
		}
		System.out.println(GREEN + "\u2705 Dang nhap thanh cong! Chao mung " + loggedInUser.getString("username") + RESET);
		return loggedInUser;
	}

	private static int showMainMenu(Scanner sc, boolean isAdmin) {
		while (true) {
			System.out.println("\n===== 📜 MENU CHINH =====");
			System.out.println("1. 🎮 Bat dau choi");
			System.out.println("2. 👤 Thong tin ca nhan");
			System.out.println("3. ⏱️ Xem lich su choi");
			System.out.println("4. 📊 Thong ke nguoi choi");
			System.out.println("5. 🏆 Bang xep hang");
			if (isAdmin) {
				System.out.println("6. 🛠️ Quan tri admin");
				System.out.println("7. ❓ Quan tri cau hoi");
			}
			System.out.println("0. 🚪 Dang xuat");
			System.out.print("👉 Chon (0-" + (isAdmin ? "7" : "5") + "): ");

			String line = sc.nextLine().trim();
			if (line.isEmpty()) {
				System.out.println("⚠️ Vui long nhap so hop le.");
				continue;
			}
			int choice;
			try {
				choice = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				System.out.println("⚠️ Vui long nhap so hop le.");
				continue;
			}
			int maxOption = isAdmin ? 7 : 5;
			if (choice < 0 || choice > maxOption) {
				System.out.println("\u2753 Lua chon khong hop le, thu lai.");
				continue;
			}
			return choice;
		}
	}

	
}
