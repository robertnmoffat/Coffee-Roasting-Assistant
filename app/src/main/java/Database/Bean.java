package Database;

import java.io.Serializable;

public class Bean implements Serializable {
    public int id;
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
}