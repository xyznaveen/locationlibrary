package np.com.naveenniraula.locationlibrary.util;

public class StringUtil {

    public static String trimComma(String inputValue) {
        return inputValue.replaceAll("(^(\\s*?\\,+)+\\s?)|(^\\s+)|(\\s+$)|((\\s*?\\,+)+\\s?$)", "");
    }

}
