import org.bson.Document;

/**
 * Canonical representation for the users collection.
 */
public class UserDocument {
	public static final String FIELD_USERNAME = "username";
	public static final String FIELD_EMAIL = "email";
	public static final String FIELD_PASSWORD = "password";
	public static final String FIELD_MONEY = "money";
	public static final String FIELD_ROLE = "role";
	public static final String FIELD_ACTIVE = "is_active";

	private final String username;
	private final String email;
	private final String password;
	private final double money;
	private final String role;
	private final boolean active;

	public UserDocument(String username, String email, String password, double money, String role, boolean active) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.money = money;
		this.role = role;
		this.active = active;
	}

	public Document toDocument() {
		return new Document(FIELD_USERNAME, username).append(FIELD_EMAIL, email).append(FIELD_PASSWORD, password)
				.append(FIELD_MONEY, money).append(FIELD_ROLE, role).append(FIELD_ACTIVE, active);
	}

	public static UserDocument fromDocument(Document document) {
		if (document == null) {
			return null;
		}
		Object moneyValue = document.get(FIELD_MONEY);
		double money = moneyValue instanceof Number ? ((Number) moneyValue).doubleValue() : 0.0;

		return new UserDocument(document.getString(FIELD_USERNAME), document.getString(FIELD_EMAIL),
				document.getString(FIELD_PASSWORD), money, document.getString(FIELD_ROLE),
				document.getBoolean(FIELD_ACTIVE, Boolean.TRUE));
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public double getMoney() {
		return money;
	}

	public String getRole() {
		return role;
	}

	public boolean isActive() {
		return active;
	}
}
