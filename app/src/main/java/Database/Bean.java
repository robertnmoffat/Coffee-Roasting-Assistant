package Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import Utilities.ObjectToStringConverter;

public class Bean extends DbData implements Serializable  {
    public int id;
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
        typeName = "bean";
        id = 0;
        serverId = 0;
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

    public Bean(JSONObject json) throws JSONException {
        typeName = "bean";
        id = 0;
        serverId = Integer.parseInt(json.getString("bean_id"));
        name= json.getString("bean_name");
        origin= json.getString("bean_origin");
        farm= json.getString("bean_farm");
        dryingMethod= json.getString("bean_drying_method");
        process= json.getString("bean_process_style");
        flavours=json.getString("bean_flavour");
        altitude=json.getString("bean_altitude");
        body=json.getString("bean_body");
        acidity=json.getString("bean_acidity");
        pricePerPound=Float.parseFloat(json.getString("bean_price_per_pound"));
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
