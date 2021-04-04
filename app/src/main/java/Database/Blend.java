package Database;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Blend {
    public int id=0;
    public String name="";
    public String description="";
    public ArrayList<Roast> roasts;

    public Blend(){
        roasts = new ArrayList<Roast>();
    }
}
