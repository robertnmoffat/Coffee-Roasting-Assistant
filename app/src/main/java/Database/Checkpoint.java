package Database;

import android.widget.TextView;

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
    public String name;
    public trig trigger;
    public int temperature;
    public int minutes;
    public int seconds;

    public Checkpoint(){
        id=0;
        name="";
        trigger=trig.Temperature;
        temperature=0;
        minutes=0;
        seconds=0;
    }

    public int timeTotalToSeconds(){
        return minutes*60+seconds;
    };

    public HashMap<String, String> toMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("trigger", trigger.toString());
        map.put("temperature", ""+temperature);
        map.put("time", ""+timeTotalToSeconds());
        return map;
    }
}
