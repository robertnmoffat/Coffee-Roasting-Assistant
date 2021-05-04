package Database;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Blend extends DbData {
    public int id=0;
    public String name="";
    public String description="";
    public ArrayList<Roast> roasts;

    public Blend(){
        roasts = new ArrayList<Roast>();
    }

    @Override
    public HashMap<String, String> toMap() {
        return null;
    }
}
