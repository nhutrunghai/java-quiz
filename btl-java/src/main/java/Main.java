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
				HandleQuestion.Result result = HandleQuestion.play(sc);
				if (result != null) {
					System.out.println("\n===== KET QUA =====");
					System.out.println("Nguoi choi: " + loggedInUser.getString("username"));
					System.out.println("So cau dung: " + result.getCorrectCount());
					System.out.println("Tien thuong: " + result.getMoney() + " VND");
				}
				break;
			case 2:
				System.out.println(
						"\n===== LICH SU CHOI CUA " + loggedInUser.getString("username").toUpperCase() + " =====");
				HandleHistory.showHistory(loggedInUser.getObjectId("_id").toHexString(),
						loggedInUser.getString("username"));
				break;
			case 3:
				HandlePlayer.showPlayerStats(loggedInUser.getObjectId("_id").toHexString(),
						loggedInUser.getString("username"));
				break;
			case 4:
				System.out.println("Bang xep hang tam thoi chua ho tro.");
				break;
			case 5:
				handleAdminMenu(sc, userService);
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
		System.out.println(GREEN + "Dang nhap thanh cong! Chao mung " + loggedInUser.getString("username") + RESET);
		return loggedInUser;
	}

	private static int showMainMenu(Scanner sc, boolean isAdmin) {
		while (true) {
			System.out.println("\n===== MENU CHINH =====");
			System.out.println("1. Bat dau choi");
			System.out.println("2. Xem lich su choi");
			System.out.println("3. Thong ke nguoi choi");
			System.out.println("4. Bang xep hang");
			if (isAdmin) {
				System.out.println("5. Quan tri admin");
			}
			System.out.println("0. Dang xuat");
			System.out.print("Chon (0-" + (isAdmin ? "5" : "4") + "): ");
			if (!sc.hasNextInt()) {
				System.out.println("Vui long nhap so hop le.");
				sc.nextLine();
				continue;
			}
			int choice = sc.nextInt();
			sc.nextLine(); // clear newline
			int maxOption = isAdmin ? 5 : 4;
			if (choice < 0 || choice > maxOption) {
				System.out.println("Lua chon khong hop le, thu lai.");
				continue;
			}
			return choice;
		}
	}

	private static void handleAdminMenu(Scanner sc, Connection userService) {
		boolean back = false;
		while (!back) {
			System.out.println("\n===== HE THONG QUAN LI NGUOI DUNG =====");
			System.out.println("1. Hien thi danh sach nguoi dung");
			System.out.println("2. Them nguoi dung moi");
			System.out.println("3. Cap nhat tien va trang thai");
			System.out.println("4. Xoa nguoi dung");
			System.out.println("0. Thoat chuong trinh");
			System.out.print("Chon chuc nang (0-4): ");

			if (!sc.hasNextInt()) {
				System.out.println("Vui long chi nhap so!");
				sc.nextLine();
				continue;
			}

			int selected = sc.nextInt();
			sc.nextLine(); // clear newline

			switch (selected) {
			case 1:
				userService.displayAllUsers();
				break;
			case 2:
				System.out.print("Nhap username: ");
				String user = sc.nextLine();
				System.out.print("Nhap email: ");
				String email = sc.nextLine();
				System.out.print("Nhap password: ");
				String pass = sc.nextLine();

				Double money = null;
				while (money == null) {
					System.out.print("Nhap so tien: ");
					if (sc.hasNextDouble()) {
						money = sc.nextDouble();
						sc.nextLine();
					} else {
						System.out.println("Vui long nhap mot so hop le.");
						sc.nextLine();
					}
				}

				System.out.print("Nhap vai tro (ADMIN/USER): ");
				String role = sc.nextLine();
				userService.addUser(user, email, pass, money, role);
				break;
			case 3:
				System.out.print("Nhap username can sua: ");
				String uUpdate = sc.nextLine();

				Double newMoney = null;
				while (newMoney == null) {
					System.out.print("Nhap so tien moi: ");
					if (sc.hasNextDouble()) {
						newMoney = sc.nextDouble();
						sc.nextLine();
					} else {
						System.out.println("Vui long nhap mot so hop le.");
						sc.nextLine();
					}
				}

				Boolean active = null;
				while (active == null) {
					System.out.print("Kich hoat tai khoan? (true/false): ");
					String activeInput = sc.nextLine().trim().toLowerCase();
					if (activeInput.equals("true") || activeInput.equals("false")) {
						active = Boolean.parseBoolean(activeInput);
					} else {
						System.out.println("Nhap true hoac false.");
					}
				}

				// userService.updateUserStatus(uUpdate, newMoney, active);
				break;
			case 4:
				System.out.print("Nhap username can xoa: ");
				String uDelete = sc.nextLine();
				userService.deleteUser(uDelete);
				break;
			case 0:
				System.out.println("Dang thoat khoi menu admin...");
				back = true;
				break;
			default:
				System.out.println("Lua chon khong hop le!");
			}
		}
	}
}
