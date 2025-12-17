import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class History {
	private static final String FILE_NAME = "history.txt";

	public static void addRecord(String player, int correct, int money) {
		try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
			String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			fw.write(player + "|" + correct + "|" + money + "|" + time + "\n");
		} catch (IOException e) {
			System.out.println("❌ Lỗi khi ghi lịch sử: " + e.getMessage());
		}
	}

	public static void showHistory(String player) {
		System.out.println("\n===== 📜 LỊCH SỬ CHƠI CỦA " + player.toUpperCase() + " =====");
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
			String line;
			boolean found = false;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\\|");
				if (parts.length == 4 && parts[0].equalsIgnoreCase(player)) {
					System.out.printf("Ngày: %-19s | Câu đúng: %-2s | Tiền: %s VNĐ\n", parts[3], parts[1], parts[2]);
					found = true;
				}
			}
			if (!found) {
				System.out.println("🟡 Bạn chưa có lịch sử chơi nào.");
			}
		} catch (IOException e) {
			System.out.println("❌ Không thể đọc lịch sử chơi!");
		}
	}
}
