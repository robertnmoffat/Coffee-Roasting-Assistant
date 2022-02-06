package Database;

import java.util.HashMap;


/**
 * Class containing information about a coffee roasting machine.
 */
public class Roaster extends DbData{
    public int id=0;
    public String name;
    public String description;
    public String brand;
    public float capacityPounds;
    public String heatingType;
    public float drumSpeed;

    /**
     * Create blank Roaster object.
     */
    public Roaster() {
        id = 0;
        name = "";
        description = "";
        brand = "";
        capacityPounds = 0.0f;
        heatingType = "";
        drumSpeed = 0.0f;
    }

    /**
     * Represent Roaster information in the form of a HashMap.
     * @return Roaster information HashMap
     */
    @Override
    public HashMap<String, String> toMap() {
        //TODO if needed for server
        return null;
    }
}
