package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database.Bean;
import Database.Checkpoint;

public class Roast extends DbData implements Serializable {
    public int id;
    public Bean bean;
    public String roastLevel;
    public int chargeTemp;
    public int dropTemp;
    public String flavour;
    public List<Checkpoint> checkpoints;

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
