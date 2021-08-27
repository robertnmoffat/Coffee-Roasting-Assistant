package Database;

import java.util.Calendar;
import java.util.HashMap;

public class RoastRecord extends DbData{
    public int id=0;
    public Roast roastProfile;
    public String filename;
    public int filesizeBytes=0;
    public float startWeightPounts;
    public float endWeightPounds;
    public String dateTime;
    public Roaster roaster;

    public RoastRecord(){
        filename = "";
        roastProfile = null;
        this.startWeightPounts = 0.0f;
        this.endWeightPounds = 0.0f;
        roaster = null;
        this.dateTime = Calendar.getInstance().getTime().toString();
    }

    public RoastRecord(Roast roastProfile, float startWeightPounts, float endWeightPounds){
        this.roastProfile = roastProfile;
        this.startWeightPounts = startWeightPounts;
        this.endWeightPounds = endWeightPounds;
        this.dateTime = Calendar.getInstance().getTime().toString();
    }


    @Override
    public HashMap<String, String> toMap() {
        //TODO if needed outside of server. wont be used in server.
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        return map;
    }
}
