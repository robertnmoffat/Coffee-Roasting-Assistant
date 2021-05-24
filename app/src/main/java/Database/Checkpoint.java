package Database;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import Utilities.ObjectToStringConverter;

public class Checkpoint extends DbData implements Serializable {
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

    public Checkpoint(){
        typeName = "checkpoint";
        id=0;
        name="";
        trigger=trig.Temperature;
        temperature=0;
        minutes=0;
        seconds=0;
    }

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

    public int timeTotalInSeconds(){
        return minutes*60+seconds;
    };

    public HashMap<String, String> toMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("trigger", trigger.toString());
        map.put("time", ""+timeTotalInSeconds());
        map.put("temperature", ""+temperature);
        return map;
    }
}
