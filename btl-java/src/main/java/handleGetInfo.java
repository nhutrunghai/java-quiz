import java.text.DecimalFormat;

import org.bson.Document;

public class handleGetInfo {

    /**
     * Lay thong tin nguoi dung tu Connection dua tren id nhap vao.
     */
    public static void showInfo(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("Khong co thong tin nguoi dung.");
            return;
        }

        Connection connection = new Connection();
        Document user = connection.findUserById(userId);
        if (user == null) {
            System.out.println("Khong tim thay thong tin nguoi dung.");
            return;
        }

        String username = user.getString("username");
        Number moneyObj = user.get("money", Number.class);
        double moneyValue = moneyObj == null ? 0 : moneyObj.doubleValue();
        String money = new DecimalFormat("#,###.##").format(moneyValue);
        String role = user.getString("role");
        String email = user.getString("email");

        String border = "================ 📇 Thong tin ================";
        System.out.println(border);
        System.out.printf("=  %-10s: %-27s %n", "👤 username", username == null ? "" : username);
        System.out.printf("=  %-10s: %-27s %n", "💰 money", money + " VND");
        System.out.printf("=  %-10s: %-27s %n", "🛡️ role", role == null ? "" : role);
        System.out.printf("=  %-10s: %-27s %n", "📧 email", email == null ? "" : email);
        System.out.println("==============================================");
    }
}
