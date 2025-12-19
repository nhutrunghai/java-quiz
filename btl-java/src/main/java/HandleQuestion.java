import java.util.Scanner;
import java.time.Instant;
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
		private final String status;
		private final Boolean isSafeWin;
		private final Instant startTime ;
		private final Instant endTime = Instant.now();
        public Result(int correctCount, int money, String status, Boolean isSafeWin, Instant startTime) {
            this.correctCount = correctCount;
            this.money = money;
			this.status = status;
			this.isSafeWin = isSafeWin;
			this.startTime = startTime;
        }
        public int getCorrectCount() { return correctCount; }
        public int getMoney() { return money; }
        public String getStatus() { return status; }
        public Boolean getIsSafeWin() { return isSafeWin; }
		public Instant getStartTime() { return startTime; }
		public Instant getEndTime() { return endTime; }
    }

    public static Result play(Scanner sc) {
        QuestionConnection repo = new QuestionConnection();
        int correctCount = 0;
        int money = 0;
		Instant startTime = Instant.now();
        boolean help5050 = true;
        boolean helpCall = true;
        boolean helpChange = true;
        boolean helpAudience = true;

        for (int i = 1; i <= 15; i++) {
            Question q = repo.getOneRandomQuestionByLevel(i);
            if (q == null) {
                System.out.println("Hết câu hỏi!");
                break;
            }

            boolean[] mask = {true, true, true, true}; 
            boolean questionResolved = false; 

            while (!questionResolved) {
                System.out.println("\n" + YELLOW + "🟡 CAU " + i + " [" + REWARDS[i] + " VND] 🟡" + RESET);
                System.out.println(q.getQuestion());
                printOptions(q.getOptions(), mask);

                System.out.println("\n--- 🆘 TRO GIUP ---");
                if (help5050) System.out.print("[5] 🔀 50/50  ");
                if (helpCall) System.out.print("[6] 📞 Goi dien  ");
                if (helpChange) System.out.print("[7] 🔄 Doi cau  ");
                if (helpAudience) System.out.print("[8] 👥 Khan gia  ");
                System.out.println("\n[0] ✋ Dung cuoc choi");
                System.out.print("Lua chon cua ban (A/B/C/D hoac so): ");

                String choice = sc.nextLine().trim().toUpperCase();
                if (choice.isEmpty()) continue;

                switch (choice) {
                    case "5":
                        if (help5050) {
                            mask = Help.fiftyFiftyMask(q);
                            help5050 = false;
                            System.out.println(GREEN + ">>> ✅ Da loai bo 2 phuong an sai." + RESET);
                        } else System.out.println(RED + "❌ Da dung roi!" + RESET);
                        break;

                    case "6":
                        if (helpCall) {

                            char correctAns = q.getCorrect(); 
                            helpCall = false;
                            System.out.println(GREEN + ">>> 📞 Nguoi than: Toi tin dap an dung la " + correctAns + RESET);
                        } else System.out.println(RED + "❌ Da dung roi!" + RESET);
                        break;

                    case "7":
                        if (helpChange) {
                            System.out.println(YELLOW + ">>> 🔄 Dang tim mot cau hoi khac cung muc do..." + RESET);
                            

                            Question newQuestion = repo.getOneRandomQuestionByLevel(i);
                            

                            if (newQuestion != null && newQuestion.getQuestion().equals(q.getQuestion())) {
                                newQuestion = repo.getOneRandomQuestionByLevel(i); 
                            }

                            if (newQuestion != null) {
                                q = newQuestion;
                                mask = new boolean[]{true, true, true, true}; 
                                helpChange = false;
                                System.out.println(GREEN + ">>> ✅ Da doi cau hoi thanh cong!" + RESET);
                            } else {
                                System.out.println(RED + "❌ Khong con cau hoi nao khac o muc nay!" + RESET);
                            }
                        } else System.out.println(RED + "❌ Da dung roi!" + RESET);
                        break;

                    case "8":
                        if (helpAudience) {
                            int[] p = Help.audiencePoll(q);
                            System.out.println(GREEN + ">>> 👥 Khan gia: A:" + p[0] + "% | B:" + p[1] + "% | C:" + p[2] + "% | D:" + p[3] + "%" + RESET);
                            helpAudience = false;
                        } else System.out.println(RED + "❌ Da dung roi!" + RESET);
                        break;

                    case "0":
                        return new Result(correctCount, money,"Dừng chơi",true, startTime	);

                    case "A": case "B": case "C": case "D":
                        char userAns = choice.charAt(0);
                        if (userAns == q.getCorrect()) {
                            System.out.println(GREEN + "✅ CHÍNH XÁC!" + RESET);
                            correctCount++;
                            money = REWARDS[correctCount];
                            questionResolved = true; 
                            
                        } else {
							
                            System.out.println(RED + "❌ SAI RỒI! Đáp án là: " + q.getCorrect() + RESET);
                            return new Result(correctCount, 0, "Trả lời sai", false, startTime);
                        }
                        break;

                    default:
                        System.out.println(RED + "Lựa chọn không hợp lệ!" + RESET);
                        break;
                }
            }
        }
        return new Result(correctCount, money, "Chiến thắng", true, startTime);
    }

    private static void printOptions(String[] opts, boolean[] mask) {
        char label = 'A';
        for (int i = 0; i < 4; i++) {
            System.out.println(label + ". " + (mask[i] ? opts[i] : "---"));
            label++;
        }
    }
}
