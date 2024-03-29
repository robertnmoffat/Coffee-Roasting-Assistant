package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class containing coffee blend information.
 */
public class Blend extends DbData {
    public int id=0;
    public String description="";
    public ArrayList<Roast> roasts;

    /**
     * Create a blank Blend object.
     */
    public Blend(){
        typeName = "blend";
        roasts = new ArrayList<Roast>();
    }

    /**
     * Create a Blend object according to a JSON object.
     * @param json JSON object representing a coffee blend.
     * @throws JSONException
     */
    public Blend(JSONObject json) throws JSONException {
        typeName = "blend";
        name = json.getString("blend_name");
        description = json.getString("blend_description");
        serverId = Integer.parseInt(json.getString("blend_id"));
        roasts = new ArrayList<Roast>();
    }

    /**
     * Creates a HashMap representing the information contained in this blend.
     * @return
     */
    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        return map;
    }
}
