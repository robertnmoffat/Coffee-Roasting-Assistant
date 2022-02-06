package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class representing a Roast - Checkpoint junction table in database.
 */
public class RoastCheckpointAssociation extends DbData{
    public int roastId;
    public int checkpointId;

    /**
     * Create blank object
     */
    public RoastCheckpointAssociation(){
        typeName = "checkpoints";
        roastId=0;
        checkpointId=0;
    }

    /**
     * Create RoastCheckpointAssociation according to passed JSON object.
     * @param json JSON object containing RoastCheckpointAssociation information
     * @throws JSONException
     */
    public RoastCheckpointAssociation(JSONObject json) throws JSONException {
        typeName = "checkpoints";
        roastId = json.getInt("id");
        checkpointId = json.getInt("checkpoint");
    }


    /**
     * Represent RoastCheckpointAssociation information in the form of a HashMap.
     * @return RoastCheckpointAssociation information HashMap
     */
    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("roastId", ""+roastId);
        map.put("checkpointId", ""+checkpointId);
        return map;
    }
}
