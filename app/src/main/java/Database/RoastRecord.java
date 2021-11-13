package Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class RoastRecord extends DbData{
    public int id=0;
    public Roast roastProfile;
    public String filename;
    public int filesizeBytes=0;
    public float startWeightPounds;
    public float endWeightPounds;
    public String dateTime;
    public Roaster roaster;

    public RoastRecord(){
        filename = "";
        roastProfile = null;
        this.startWeightPounds = 0.0f;
        this.endWeightPounds = 0.0f;
        roaster = null;
        this.dateTime = Calendar.getInstance().getTime().toString();
    }

    public RoastRecord(Roast roastProfile, float startWeightPounds, float endWeightPounds){
        this.roastProfile = roastProfile;
        this.startWeightPounds = startWeightPounds;
        this.endWeightPounds = endWeightPounds;
        this.dateTime = Calendar.getInstance().getTime().toString();
    }

    public void setFilesizeBytes(ArrayList<Integer> checkpointTemps, ArrayList<Integer> safeTempsOverTime){
        filesizeBytes = (checkpointTemps.size()+safeTempsOverTime.size()+2)*Integer.BYTES;
    }


    @Override
    public HashMap<String, String> toMap() {
        //TODO if needed outside of server. wont be used in server.
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        return map;
    }

    public String toString(){
        return name+" "+startWeightPounds+" "+endWeightPounds;
    }
}
