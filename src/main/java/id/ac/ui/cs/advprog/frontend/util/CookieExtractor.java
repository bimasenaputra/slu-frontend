package id.ac.ui.cs.advprog.frontend.util;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CookieExtractor {

    public static Map<String, String> extract(String cookie) {
        var regex = "([^=]+)=([^\\;]+);\\s?";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(cookie);

        Map<String, String> result = new HashMap<>();
        while (matcher.find()) {
            result.put(matcher.group(1), matcher.group(2));
        }
        return result;
    }
}
