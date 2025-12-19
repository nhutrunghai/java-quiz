
public class Question {
	private String question;
	private String[] options; 
	private char correct;

	public Question(String question, String a, String b, String c, String d, char correct) {
		this.question = question;
		this.options = new String[] { a, b, c, d };
		this.correct = Character.toUpperCase(correct);
	}

	public String getQuestion() {
		return question;
	}

	public String[] getOptions() {
		return options;
	} 

	public char getCorrect() {
		return correct;
	}
}
