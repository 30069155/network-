public class UserA {
    public static void main(String[] args) {
        //connect to the switch a
        try {
            UserSession.start("UserA", "localhost", 5100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
