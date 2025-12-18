import java.time.Duration;
import java.util.Date;

import org.bson.Document;

/**
 * Tổng hợp thống kê lịch sử chơi của một người chơi.
 */
public class HandlePlayer {

	/**
	 * Hiển thị thống kê: số trận, tổng thời gian chơi, trung bình số câu trả lời/trận.
	 */
	public static void showPlayerStats(String userId, String username) {
		HistoryConnection historyConn = new HistoryConnection();
		Iterable<Document> docs = historyConn.findHistoryByUser(userId, Integer.MAX_VALUE);

		int totalGames = 0;
		long totalSeconds = 0;
		int totalQuestions = 0;

		for (Document doc : docs) {
			totalGames++;
			Number levelNum = doc.get("highest_level", Number.class);
			totalQuestions += levelNum != null ? levelNum.intValue() : 0;

			Date start = doc.getDate("start_time");
			Date end = doc.getDate("end_time");
			if (start != null && end != null) {
				totalSeconds += Duration.between(start.toInstant(), end.toInstant()).getSeconds();
			}
		}

		if (totalGames == 0) {
			System.out.println("ℹ️ Khong co lich su choi nao cho nguoi dung: " + username);
			return;
		}

		int avgQuestions = totalGames > 0 ? (int) Math.round((double) totalQuestions / totalGames) : 0;
		String totalTimeFormatted = formatDuration(totalSeconds);

		System.out.println(String.format("%-4s | %-15s | %-15s | %-20s | %-15s | %-15s", "STT", "Username", "🎮 Tong tran",
				"⏱️ Tong thoi gian", "❓ Tong so cau", "📊 Trung binh cau"));
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
		System.out.println(String.format("%-4d | %-15s | %-15d | %-20s | %-15d | %-15d", 1, username, totalGames,
				totalTimeFormatted, totalQuestions, avgQuestions));
	}

	private static String formatDuration(long totalSeconds) {
		long days = totalSeconds / (24 * 3600);
		long remainder = totalSeconds % (24 * 3600);
		long hours = remainder / 3600;
		remainder %= 3600;
		long minutes = remainder / 60;
		long seconds = remainder % 60;

		StringBuilder sb = new StringBuilder();
		if (days > 0)
			sb.append(days).append("d ");
		if (hours > 0 || days > 0)
			sb.append(hours).append("h ");
		if (minutes > 0 || hours > 0 || days > 0)
			sb.append(minutes).append("m ");
		sb.append(seconds).append("s");
		return sb.toString().trim();
	}
}
