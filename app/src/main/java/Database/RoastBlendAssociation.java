package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RoastBlendAssociation extends DbData{
    public int roast_profile_id;
    public int blend_id;

    public RoastBlendAssociation(){
        typeName = "roast_blend";
        roast_profile_id = 0;
        blend_id = 0;
    }

    public RoastBlendAssociation(JSONObject json) throws JSONException {
        typeName = "roast_blend";
        roast_profile_id = json.getInt("roast_profile_id");
        blend_id = json.getInt("blend_id");
    }

    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("roast_profile_id", ""+roast_profile_id);
        map.put("blend_id", ""+blend_id);
        return map;
    }
}
