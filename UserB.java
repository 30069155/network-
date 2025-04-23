public class UserB {
    public static void main(String[] args) {
        //connect to the switch b
        try {
            UserSession.start("UserB", "localhost", 6100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
