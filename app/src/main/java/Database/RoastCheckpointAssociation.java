package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RoastCheckpointAssociation extends DbData{
    public int roastId;
    public int checkpointId;

    public RoastCheckpointAssociation(){
        typeName = "checkpoints";
        roastId=0;
        checkpointId=0;
    }

    public RoastCheckpointAssociation(JSONObject json) throws JSONException {
        typeName = "checkpoints";
        roastId = json.getInt("id");
        checkpointId = json.getInt("checkpoint");
    }


    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("roastId", ""+roastId);
        map.put("checkpointId", ""+checkpointId);
        return map;
    }
}
