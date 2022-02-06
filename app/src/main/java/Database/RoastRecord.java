package Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Class containing information about a roasting session.
 */
public class RoastRecord extends DbData{
    public int id=0;
    public Roast roastProfile;
    public String filename;
    public int filesizeBytes=0;
    public float startWeightPounds;
    public float endWeightPounds;
    public String dateTime;
    public Roaster roaster;

    /**
     * Create blank RoastRecord object.
     */
    public RoastRecord(){
        filename = "";
        roastProfile = null;
        this.startWeightPounds = 0.0f;
        this.endWeightPounds = 0.0f;
        roaster = null;
        this.dateTime = Calendar.getInstance().getTime().toString();
    }

    /**
     * Create new RoastRecord object according to passed information.
     * @param roastProfile RoastProfile used in this roast
     * @param startWeightPounds Starting weight of beans before roasting
     * @param endWeightPounds Final weight of beans after roasting
     */
    public RoastRecord(Roast roastProfile, float startWeightPounds, float endWeightPounds){
        this.roastProfile = roastProfile;
        this.startWeightPounds = startWeightPounds;
        this.endWeightPounds = endWeightPounds;
        this.dateTime = Calendar.getInstance().getTime().toString();
    }

    /**
     * Set filesize for saving. based on size of checkpoint and temperature arrays.
     * @param checkpointTemps checkpoint array that will be saved
     * @param safeTempsOverTime temperature array that will be saved
     */
    public void setFilesizeBytes(ArrayList<Integer> checkpointTemps, ArrayList<Integer> safeTempsOverTime){
        filesizeBytes = (checkpointTemps.size()+safeTempsOverTime.size()+2)*Integer.BYTES;
    }

    /**
     * Represent RoastRecord information in the form of a HashMap.
     * @return RoastRecord information HashMap
     */
    @Override
    public HashMap<String, String> toMap() {
        //TODO if needed outside of server. wont be used in server.
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        return map;
    }

    /**
     * Converts RoastRecord information into a string.
     * @return String representing this RoastRecord object.
     */
    public String toString(){
        return name+" "+startWeightPounds+" "+endWeightPounds;
    }
}
