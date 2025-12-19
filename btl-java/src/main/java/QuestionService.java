import static com.mongodb.client.model.Filters.eq;

import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

public class QuestionService {
	private MongoCollection<Document> qCollection;

	public QuestionService() {

		QuestionConnection conn = new QuestionConnection();


		this.qCollection = conn.getqCollection();

	}


	public void addQuestion(int id, int level, String text, Map<String, String> opts, String correct) {
		Document optionsObj = new Document().append("A", opts.get("A")).append("B", opts.get("B"))
				.append("C", opts.get("C")).append("D", opts.get("D"));

		Document q = new Document("question_id", id).append("level", level).append("question_text", text)
				.append("options", optionsObj) // Lưu dưới dạng Object {A:..., B:...}
				.append("correct_answer", correct.toUpperCase());

		qCollection.insertOne(q);
	}



	public boolean checkID(int id) {
		Document doc = qCollection.find(eq("question_id", id)).first();
		return doc != null;
	}


	public void displayAllQuestions() {
		System.out.println("\n--- 📚 DANH SÁCH CÂU HỎI ---");
		System.out.println(String.format("%-5s | %-5s | %-100s | %-10s", "🆔 ID", "⭐ Level", "📄 Text", "✅ Correct"));
		System.out.println("----------------------------------------------------------------------------------"
				+ "-----------------------------------------");
		for (Document doc : qCollection.find()) {
			int id = doc.getInteger("question_id");


			int lvl = doc.getInteger("level", 0);

			String text = doc.getString("question_text");
			String correct = doc.getString("correct_answer");
			System.out.println(String.format("%-5d | %-5d | %-100s | %-10s", id, lvl, text, correct));
		}
	}


	public void updateQuestion(int id, int level, String text, Map<String, String> opts, String correct) {
		Document optionsObj = new Document()
				.append("A", opts.get("A"))
				.append("B", opts.get("B"))
				.append("C", opts.get("C"))
				.append("D", opts.get("D"));

		qCollection.updateOne(eq("question_id", id),
				Updates.combine(Updates.set("level", level), Updates.set("question_text", text),
						Updates.set("options", optionsObj), Updates.set("correct_answer", correct.toUpperCase())));
	}


	public void deleteQuestion(int id) {
		qCollection.deleteOne(eq("question_id", id));
		System.out.println("❌ Đã xóa câu hỏi ID: " + id);
	}

}
