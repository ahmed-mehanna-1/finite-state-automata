public class Utils {
    public static boolean isDigit(String num) {
        try {
            int x = Integer.parseInt(num);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
