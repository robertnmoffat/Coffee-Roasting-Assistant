package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Blend extends DbData {
    public int id=0;
    public String description="";
    public ArrayList<Roast> roasts;

    public Blend(){
        typeName = "blend";
        roasts = new ArrayList<Roast>();
    }

    public Blend(JSONObject json) throws JSONException {
        typeName = "blend";
        name = json.getString("blend_name");
        description = json.getString("blend_description");
        serverId = Integer.parseInt(json.getString("blend_id"));
        roasts = new ArrayList<Roast>();
    }

    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        return map;
    }
}
