package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database.Bean;
import Database.Checkpoint;

/**
 * Class containing information pertaining to a coffee roast.
 */
public class Roast extends DbData implements Serializable {
    public int id;
    public Bean bean;
    public String roastLevel;
    public int chargeTemp;
    public int dropTemp;
    public String flavour;
    public List<Checkpoint> checkpoints;

    /**
     * Create a blank roast object.
     */
    public Roast(){
        typeName = "roast";
        id=0;
        name="";
        bean=null;
        roastLevel="";
        chargeTemp = 0;
        dropTemp=0;
        flavour="";
        checkpoints = new ArrayList<Checkpoint>();
    }

    /**
     * Create a roast according to passed JSON object.
     * @param json JSON object containing roast information
     * @throws JSONException
     */
    public Roast(JSONObject json) throws JSONException {
        typeName = "roast";
        id=0;
        serverId = Integer.parseInt(json.getString("roast_profile_id"));
        name = json.getString("roast_profile_name");
        bean = null;
        roastLevel = json.getString("roast");
        chargeTemp = Integer.parseInt(json.getString("charge_temp"));
        dropTemp = Integer.parseInt(json.getString("drop_temp"));
        flavour = json.getString("flavour");
        checkpoints = new ArrayList<Checkpoint>();
    }

    /**
     * Represent roast information in the form of a HashMap.
     * @return roast information HashMap
     */
    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("bean", ""+bean.serverId);
        map.put("roastLevel", roastLevel);
        map.put("chargeTemp", ""+chargeTemp);
        map.put("dropTemp", ""+dropTemp);
        map.put("flavour", flavour);

        return map;
    }
}
