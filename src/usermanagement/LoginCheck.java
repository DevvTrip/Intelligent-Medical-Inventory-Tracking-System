package usermanagement;

public class LoginCheck {

    public static User check(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String user = username.trim().toLowerCase();
        if (user.equals("admin") && password.equals("admin123")) {
            return new User("admin", "ADMIN");
        }
        if (user.equals("pharma") && password.equals("pharma123")) {
            return new User("pharma", "PHARMACIST");
        }
        return null;
    }
}
