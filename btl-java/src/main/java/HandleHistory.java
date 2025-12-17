import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.bson.Document;

/**
 * Hiển thị lịch sử chơi cho người dùng.
 */
public class HandleHistory {

	/**
	 * In tối đa 10 bản ghi lịch sử cho user, dạng bảng.
	 */
	public static void showHistory(String userId, String username) {
		HistoryConnection historyConn = new HistoryConnection();
		Iterable<Document> docs = historyConn.findHistoryByUser(userId, 10);
		
		System.out.println(String.format("%-4s | %-15s | %-12s | %-5s | %-12s | %-15s", "STT", "Username", "Tien",
				"Level", "Trang thai", "Thoi gian"));
		System.out.println("----------------------------------------------------------------------------");

		boolean found = false;
		Instant now = Instant.now();
		int stt = 1;
		for (Document doc : docs) {
			found = true;
			Number moneyNum = doc.get("final_score", Number.class);
			int money = moneyNum != null ? (int) Math.round(moneyNum.doubleValue()) : 0;

			Number levelNum = doc.get("highest_level", Number.class);
			int level = levelNum != null ? levelNum.intValue() : 0;

			String status = doc.getString("game_status");
			java.util.Date end = doc.getDate("end_time");
			String timeLabel = formatRelativeTime(now, end == null ? null : end.toInstant());

			System.out.println(String.format("%-4d | %-15s | %-12d | %-5d | %-12s | %-15s", stt, username, money, level,
					status, timeLabel));
			stt++;
		}
		if (!found) {
			System.out.println("🟡 Khong co lich su choi nao.");
		}

	}

	private static String formatRelativeTime(Instant now, Instant target) {
		if (target == null) {
			return "N/A";
		}
		Duration duration = Duration.between(target, now);
		long days = duration.toDays();
		if (days >= 1) {
			if (days == 1) {
				return "Hom qua";
			} else if (days < 2) {
				return "Hom kia";
			} else if (days < 7) {
				return String.format("%d ngay", days);
			} else {
				return DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(target);
			}
		}
		long hours = duration.toHours();
		if (hours >= 1) {
			return String.format("%d gio", hours);
		}
		long minutes = duration.toMinutes();
		if (minutes >= 1) {
			return String.format("%d phut", minutes);
		}
		long seconds = duration.getSeconds();
		return String.format("%d giay", seconds);
	}
}
