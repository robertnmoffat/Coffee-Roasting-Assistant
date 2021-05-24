package Database;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import Utilities.ObjectToStringConverter;

public abstract class DbData{
    public String name;
    public String typeName;
    public int serverId = 0;
    public abstract HashMap<String, String> toMap();

    public String toString(){
        try {
            return ObjectToStringConverter.createPostDataString(toMap());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
