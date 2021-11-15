package Database;

import java.util.HashMap;



public class Roaster extends DbData{
    public int id=0;
    public String name;
    public String description;
    public String brand;
    public float capacityPounds;
    public String heatingType;
    public float drumSpeed;


    public Roaster() {
        id = 0;
        name = "";
        description = "";
        brand = "";
        capacityPounds = 0.0f;
        heatingType = "";
        drumSpeed = 0.0f;
    }



    @Override
    public HashMap<String, String> toMap() {
        //TODO if needed for server
        return null;
    }
}
