package Utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ObjectToStringConverter {

    /**
     * Converts the passed map into a string to be used in an http post request.
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createPostDataString(HashMap<String, String> map) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first=true;
        for(Map.Entry<String, String> entry : map.entrySet()){
            if(first)
                first=false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
