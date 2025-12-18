import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ManageQuestions {
	private QuestionService dbContext;
	private Scanner scanner;

	public ManageQuestions(QuestionService dbContext, Scanner scanner) {
		this.dbContext = dbContext;
		this.scanner = scanner;
	}

	public void showMenu() {
		while (true) {
			System.out.println("\n===== 🧠 QUAN LY KHO CAU HOI =====");
			System.out.println("1. 📋 Hien thi danh sach");
			System.out.println("2. ✍️ Them cau hoi moi (Check trung ID)");
			System.out.println("3. 🔧 Sua cau hoi");
			System.out.println("4. 🗑️ Xoa cau hoi");
			System.out.println("0. ↩️ Quay lai");
			System.out.print("👉 Chon: ");

			String choice = scanner.nextLine();
			if (choice.equals("0"))
				break;

			switch (choice) {
			case "2": // THÊM CÂU HỎI
				System.out.println("\n--- THÊM CÂU HỎI MỚI ---");
				int id = -1;
				// 1. Kiểm tra ID
				while (true) {
					try {
						System.out.print("🔢 Nhap Question ID (so nguyen): ");
						id = Integer.parseInt(scanner.nextLine());
						if (dbContext.checkID(id)) {
							System.out.println("❌ ID đã tồn tại! Vui lòng nhập ID khác.");
						} else
							break;
					} catch (Exception e) {
						System.out.println("❌ Loi: ID phai la so nguyen!");
					}
				}

				// 2. Kiểm tra Level
				int lvl = -1;
				while (true) {
					try {
						System.out.print("⭐ Cap do (1-15): ");
						lvl = Integer.parseInt(scanner.nextLine());
						if (lvl >= 1 && lvl <= 15)
							break;
						else
							System.out.println("❌ Cap do phai tu 1-15.");
					} catch (Exception e) {
						System.out.println("❌ Loi: Cap do phai la so!");
					}
				}

				System.out.print("💬 Noi dung cau hoi: ");
				String text = scanner.nextLine();

				// 3. Nhập Options
				Map<String, String> addOpts = new HashMap<>();
				String[] labels = { "A", "B", "C", "D" };
				for (String l : labels) {
					System.out.print("🔹 Lua chon " + l + ": ");
					addOpts.put(l, scanner.nextLine());
				}

				// 4. Kiểm tra Đáp án đúng
				String correct;
				while (true) {
					System.out.print("✅ Dap an dung (A/B/C/D): ");
					correct = scanner.nextLine().toUpperCase();
					if (correct.matches("[A-D]"))
						break;
					else
						System.out.println("❌ Chi nhap A, B, C hoac D.");
				}

				dbContext.addQuestion(id, lvl, text, addOpts, correct);
				System.out.println("✅ Thêm câu hỏi thành công!");
				break;

			case "3": // SỬA CÂU HỎI
				System.out.print("\n🔢 Nhap ID cau hoi can sua: ");
				int editId;
				try {
					editId = Integer.parseInt(scanner.nextLine());
					if (!dbContext.checkID(editId)) {
						System.out.println("❌ Không tìm thấy câu hỏi với ID này!");
						break;
					}

					// Nhập các thông tin mới tương tự như case 2
					System.out.print("⭐ Cap do moi (1-15): ");
					int nLvl = Integer.parseInt(scanner.nextLine());
					System.out.print("💬 Noi dung moi: ");
					String nText = scanner.nextLine();

					Map<String, String> nOpts = new HashMap<>();
					for (String l : new String[] { "A", "B", "C", "D" }) {
						System.out.print("🔹 Lua chon " + l + ": ");
						nOpts.put(l, scanner.nextLine());
					}

					System.out.print("✅ Dap an dung moi: ");
					String nCorrect = scanner.nextLine().toUpperCase();

					dbContext.updateQuestion(editId, nLvl, nText, nOpts, nCorrect);
					System.out.println("✅ Cập nhật thành công!");
				} catch (Exception e) {
					System.out.println("❌ Lỗi định dạng dữ liệu!");
				}
				break;

			case "4": // XÓA CÂU HỎI
				System.out.print("🔢 Nhap ID can xoa: ");
				try {
					int delId = Integer.parseInt(scanner.nextLine());
					if (dbContext.checkID(delId)) {
						dbContext.deleteQuestion(delId);
						System.out.println("✅ Đã xóa câu hỏi.");
					} else {
						System.out.println("❌ ID không tồn tại.");
					}
				} catch (Exception e) {
					System.out.println("❌ ID phải là số!");
				}
				break;

			case "1":
				dbContext.displayAllQuestions();
				break;

			default:
				System.out.println("Lựa chọn sai!");
			}
		}
	}
}
