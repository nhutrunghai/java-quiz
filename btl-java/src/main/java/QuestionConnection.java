import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq; // Cần thiết để dùng eq()
import org.bson.Document;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionConnection {
    private MongoCollection<Document> collection;
	private MongoCollection<Document> qCollection;
    public QuestionConnection() {
        
        MongoClient mongoClient = MongoClients.create("mongodb+srv://nhutrunghai_db_user:SaQjyJC8xvwjpI20@cluster0.9rj7y8x.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("quiz");
        this.collection = database.getCollection("Questions");
       
        this.qCollection = this.collection;
    }
	public MongoCollection<Document> getqCollection() {
		return this.qCollection;
	}

    public Question getOneRandomQuestionByLevel(int level) {
        List<Question> list = new ArrayList<>();
        

        for (Document doc : collection.find(eq("level", level))) {
            try {
                String qText = doc.getString("question_text");
                Document optionsDoc = (Document) doc.get("options");
                String a = optionsDoc.getString("A");
                String b = optionsDoc.getString("B");
                String c = optionsDoc.getString("C");
                String d = optionsDoc.getString("D");
                
              
                String correctStr = doc.getString("correct_answer");
                char correct = (correctStr != null && !correctStr.isEmpty()) ? correctStr.charAt(0) : 'A';

                list.add(new Question(qText, a, b, c, d, correct));
            } catch (Exception e) {
                System.out.println("Lỗi khi đọc câu hỏi từ Document: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy câu hỏi nào cho Level: " + level);
            return null;
        }
        
        Collections.shuffle(list);
        return list.get(0);
    }

    /**
     * Lấy toàn bộ danh sách câu hỏi
     */
    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            Document optionsDoc = (Document) doc.get("options");
            list.add(new Question(
                doc.getString("question_text"),
                optionsDoc.getString("A"),
                optionsDoc.getString("B"),
                optionsDoc.getString("C"),
                optionsDoc.getString("D"),
                doc.getString("correct_answer").charAt(0)
            ));
        }
        return list;
    }
}
