package Database;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import Utilities.ObjectToStringConverter;

public class Bean extends DbData implements Serializable  {
    public int id;
    public int serverId;
    public String name;
    public String origin;
    public String farm;
    public String dryingMethod;
    public String process;
    public String flavours;
    public String altitude;
    public String body;
    public String acidity;
    public float pricePerPound;

    public Bean(){
        id = 0;
        name="";
        origin="";
        farm="";
        dryingMethod="";
        process="";
        flavours="";
        altitude="";
        body="";
        acidity="";
        pricePerPound=0.0f;
    }

    public HashMap<String, String> toMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("origin", origin);
        map.put("farm", farm);
        map.put("dryingMethod", dryingMethod);
        map.put("process", process);
        map.put("flavours", flavours);
        map.put("altitude", altitude);
        map.put("body", body);
        map.put("acidity", acidity);
        map.put("pricePerPound", ""+pricePerPound);
        return map;
    }
}
