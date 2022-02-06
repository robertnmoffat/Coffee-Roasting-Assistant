package Database;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import Utilities.ObjectToStringConverter;

/**
 * Class representing a checkpoint during a coffee roast.
 */
public class Checkpoint extends DbData implements Serializable {
    //Different checkpoint trigger types.
    public enum trig{
        Temperature,
        Time,
        TurnAround,
        PromptAtTemp //User must give input at checkpoint. Example would be first or second crack.
    }

    public int id;
    public trig trigger;
    public int temperature;
    public int minutes;
    public int seconds;

    /**
     * Creates a blank Checkpoint object.
     */
    public Checkpoint(){
        typeName = "checkpoint";
        id=0;
        name="";
        trigger=trig.Temperature;
        temperature=0;
        minutes=0;
        seconds=0;
    }

    /**
     * Creates a Checkpoint object according to a JSON object.
     * @param json JSON object containing Checkpoint data
     * @throws JSONException
     */
    public Checkpoint(JSONObject json) throws JSONException {
        typeName = "checkpoint";
        id=0;
        serverId = json.getInt("checkpoint_id");
        name=json.getString("checkpoint_name");
        trigger=trig.valueOf(json.getString("check_trigger"));
        temperature=json.getInt("temperature");
        int allSeconds = json.getInt("time");
        minutes=allSeconds/60;
        seconds=allSeconds-minutes*60;
    }

    /**
     * Adds minutes and seconds together as total seconds.
     * @return Total time in seconds.
     */
    public int timeTotalInSeconds(){
        return minutes*60+seconds;
    };

    /**
     * Create HashMap representing the Checkpoint object.
     * @return HashMap representing the Checkpoint
     */
    public HashMap<String, String> toMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("trigger", trigger.toString());
        map.put("time", ""+timeTotalInSeconds());
        map.put("temperature", ""+temperature);
        return map;
    }

}
