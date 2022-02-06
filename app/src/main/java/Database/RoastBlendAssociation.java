package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class representing a roast - blend junction table in database.
 */
public class RoastBlendAssociation extends DbData{
    public int roast_profile_id;
    public int blend_id;

    /**
     * Create blank object.
     */
    public RoastBlendAssociation(){
        typeName = "roast_blend";
        roast_profile_id = 0;
        blend_id = 0;
    }

    /**
     * Create object according to JSON object.
     * @param json JSON object containing table information
     * @throws JSONException
     */
    public RoastBlendAssociation(JSONObject json) throws JSONException {
        typeName = "roast_blend";
        roast_profile_id = json.getInt("roast_profile_id");
        blend_id = json.getInt("blend_id");
    }

    /**
     * Represent junction table information in the form of a HashMap.
     * @return junction information HashMap
     */
    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("roast_profile_id", ""+roast_profile_id);
        map.put("blend_id", ""+blend_id);
        return map;
    }
}
