import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * Chua logic choi cau hoi tach khoi Main de de doc hon.
 */
public class HandleQuestion {
	private static final String RESET = "\u001B[0m";
	private static final String RED = "\u001B[31m";
	private static final String YELLOW = "\u001B[33m";
	private static final String GREEN = "\u001B[32m";

	private static final int[] REWARDS = { 0, 200000, 400000, 600000, 1000000, 2000000, 3000000, 6000000, 10000000,
			14000000, 22000000, 30000000, 40000000, 60000000, 85000000, 150000000 };

	public static class Result {
		private final int correctCount;
		private final int money;

		public Result(int correctCount, int money) {
			this.correctCount = correctCount;
			this.money = money;
		}

		public int getCorrectCount() {
			return correctCount;
		}

		public int getMoney() {
			return money;
		}
	}

	public static Result play(Scanner sc) {
		ArrayList<Question> questions = loadQuestions("questions.txt");
		if (questions.isEmpty()) {
			System.out.println("Khong co cau hoi nao! Kiem tra file questions.txt.");
			return null;
		}

		boolean used50 = false, usedAudience = false, usedCall = false;
		int correctCount = 0;
		int money = 0;
		int maxQ = Math.min(15, questions.size());

		for (int i = 0; i < maxQ; i++) {
			Question q = questions.get(i);
			System.out.println(RED + "\nCau " + (i + 1) + ": " + q.getQuestion() + RESET);
			boolean[] showMask = new boolean[] { true, true, true, true };
			printOptions(q.getOptions(), showMask);

			System.out.println();
			System.out.println(YELLOW + "Nhap A/B/C/D de tra loi hoac chon so de dung tro giup:" + RESET);
			System.out.println(YELLOW + "1) 50/50   2) Hoi khan gia   3) Goi dien   0) Dung choi" + RESET);
			System.out.print("Lua chon: ");
			String choice = sc.nextLine().trim();

			if (choice.equals("1")) {
				if (used50) {
					System.out.println("Ban da dung 50/50 roi!");
					i--;
					continue;
				}
				used50 = true;
				boolean[] mask = Help.fiftyFiftyMask(q);
				printOptions(q.getOptions(), mask);
				System.out.print("Tra loi: ");
				String ans = sc.nextLine().trim().toUpperCase();
				if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
					System.out.println(GREEN + "Do. Chinh xac!" + RESET);
					correctCount++;
					money = REWARDS[correctCount];
				} else {
					System.out.println("Sai! Dap an dung: " + q.getCorrect());
					break;
				}
			} else if (choice.equals("2")) {
				if (usedAudience) {
					System.out.println("Ban da dung hoi khan gia roi!");
					i--;
					continue;
				}
				usedAudience = true;
				int[] percent = Help.audiencePoll(q);
				System.out.println(YELLOW + "\nKet qua binh chon:" + RESET);
				char cc = 'A';
				for (int p : percent) {
					System.out.printf("%c: %d%%   ", cc, p);
					cc++;
				}
				System.out.println();
				System.out.print("Tra loi: ");
				String ans = sc.nextLine().trim().toUpperCase();
				if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
					System.out.println(GREEN + "Do. Chinh xac!" + RESET);
					correctCount++;
					money = REWARDS[correctCount];
				} else {
					System.out.println("Sai! Dap an dung: " + q.getCorrect());
					break;
				}
			} else if (choice.equals("3")) {
				if (usedCall) {
					System.out.println("Ban da goi dien roi!");
					i--;
					continue;
				}
				usedCall = true;
				String[] friends = { "ban than", "me", "anh", "chi", "bac" };
				String who = friends[new Random().nextInt(friends.length)];
				char suggest = Help.callFriendSuggest(q);
				System.out.println(YELLOW + who + " goi y dap an la: " + suggest + RESET);
				System.out.print("Tra loi: ");
				String ans = sc.nextLine().trim().toUpperCase();
				if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
					System.out.println(GREEN + "Do. Chinh xac!" + RESET);
					correctCount++;
					money = REWARDS[correctCount];
				} else {
					System.out.println("Sai! Dap an dung: " + q.getCorrect());
					break;
				}
			} else if (choice.equals("0")) {
				System.out.println("Ban da dung choi. Ban nhan: " + money + " VND");
				break;
			} else {
				String ans = choice.trim().toUpperCase();
				if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
					SoundPlayer.SoundThread successThread = new SoundPlayer.SoundThread("success");
					successThread.start();
					System.out.println(GREEN + "Do. Chinh xac!" + RESET);
					correctCount++;
					money = REWARDS[correctCount];
				} else {
					SoundPlayer.SoundThread failThread = new SoundPlayer.SoundThread("fail");
					failThread.start();

					System.out.println("Sai! Dap an dung: " + q.getCorrect());
					break;
				}
			}

			if (i == maxQ - 1) {
				System.out.println(GREEN + "\nBan da hoan thanh tat ca cau hoi!" + RESET);
			}
		}

		return new Result(correctCount, money);
	}

	private static ArrayList<Question> loadQuestions(String filename) {
		ArrayList<Question> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				String[] p = line.split("\\|");
				if (p.length >= 6) {
					list.add(new Question(p[0], p[1], p[2], p[3], p[4], p[5].trim().charAt(0)));
				}
			}
		} catch (IOException e) {
			System.out.println("Loi doc file questions: " + e.getMessage());
		}
		Collections.shuffle(list);
		return list;
	}

	private static void printOptions(String[] opts, boolean[] showMask) {
		char ch = 'A';
		for (int i = 0; i < 4; i++) {
			if (showMask == null || showMask[i]) {
				System.out.println(ch + ". " + opts[i]);
			} else {
				System.out.println(ch + ". ----");
			}
			ch++;
		}
	}
}
