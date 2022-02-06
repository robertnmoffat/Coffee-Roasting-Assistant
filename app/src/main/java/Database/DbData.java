package Database;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import Utilities.ObjectToStringConverter;

/**
 * Class to be extended by any object representing a table in the database locally and on the server.
 */
public abstract class DbData{
    //Fields that are common among all database and server objects.
    public String name;
    public String typeName;
    public int serverId = 0;

    /**
     * Server objects must list all their field names and values in the form of a HashMap.
     * @return HashMap containing all objects field names and values.
     */
    public abstract HashMap<String, String> toMap();

    /**
     * Converts objects HashMap into a string for transferring to server.
     * @return
     */
    public String toString(){
        try {
            return ObjectToStringConverter.createPostDataString(toMap());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
