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
        // Trạng thái các quyền trợ giúp (mỗi game dùng 1 lần)
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

            // VÒNG LẶP XỬ LÝ TRONG 1 CÂU HỎI
            while (!questionResolved) {
                System.out.println("\n" + YELLOW + "================= CÂU " + i + " [" + REWARDS[i] + " VNĐ] =================" + RESET);
                System.out.println(q.getQuestion());
                printOptions(q.getOptions(), mask);

                System.out.println("\n--- TRỢ GIÚP ---");
                if (help5050) System.out.print("[5] 50/50  ");
                if (helpCall) System.out.print("[6] Gọi điện  ");
                if (helpChange) System.out.print("[7] Đổi câu  ");
                if (helpAudience) System.out.print("[8] Khán giả  ");
                System.out.println("\n[0] Dừng cuộc chơi");
                System.out.print("Lựa chọn của bạn (A/B/C/D hoặc số): ");

                String choice = sc.nextLine().trim().toUpperCase();
                if (choice.isEmpty()) continue;

                switch (choice) {
                    case "5":
                        if (help5050) {
                            mask = Help.fiftyFiftyMask(q);
                            help5050 = false;
                            System.out.println(GREEN + ">>> Đã loại bỏ 2 phương án sai." + RESET);
                        } else System.out.println(RED + "Đã dùng rồi!" + RESET);
                        break;

                    case "6":
                        if (helpCall) {
                            // Lấy trực tiếp đáp án đúng từ câu hỏi hiện tại
                            char correctAns = q.getCorrect(); 
                            helpCall = false;
                            System.out.println(GREEN + ">>> Người thân: Tôi tin chắc đáp án đúng là " + correctAns + RESET);
                        } else System.out.println(RED + "Đã dùng rồi!" + RESET);
                        break;

                    case "7":
                        if (helpChange) {
                            System.out.println(YELLOW + ">>> Đang tìm một câu hỏi khác cùng mức độ..." + RESET);
                            
                            // Lấy một câu hỏi mới. 
                            // QuestionRepository của bạn nên có logic để không bốc trúng q cũ
                            Question newQuestion = repo.getOneRandomQuestionByLevel(i);
                            
                            // Kiểm tra nếu chẳng may bốc trùng (nếu repo chưa xử lý loại trừ)
                            if (newQuestion != null && newQuestion.getQuestion().equals(q.getQuestion())) {
                                newQuestion = repo.getOneRandomQuestionByLevel(i); 
                            }

                            if (newQuestion != null) {
                                q = newQuestion;
                                mask = new boolean[]{true, true, true, true}; // Reset mask cho câu mới
                                helpChange = false;
                                System.out.println(GREEN + ">>> Đã đổi câu hỏi thành công!" + RESET);
                            } else {
                                System.out.println(RED + "Không còn câu hỏi nào khác ở mức này!" + RESET);
                            }
                        } else System.out.println(RED + "Đã dùng rồi!" + RESET);
                        break;

                    case "8":
                        if (helpAudience) {
                            int[] p = Help.audiencePoll(q);
                            System.out.println(GREEN + ">>> Khán giả: A:" + p[0] + "% | B:" + p[1] + "% | C:" + p[2] + "% | D:" + p[3] + "%" + RESET);
                            helpAudience = false;
                        } else System.out.println(RED + "Đã dùng rồi!" + RESET);
                        break;

                    case "0":
                        return new Result(correctCount, money,"Dừng chơi",true, startTime	);

                    case "A": case "B": case "C": case "D":
                        char userAns = choice.charAt(0);
                        if (userAns == q.getCorrect()) {
                            System.out.println(GREEN + "✅ CHÍNH XÁC!" + RESET);
                            correctCount++;
                            money = REWARDS[correctCount];
                            questionResolved = true; // Thoát while -> Tự động sang câu for tiếp theo
                            
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