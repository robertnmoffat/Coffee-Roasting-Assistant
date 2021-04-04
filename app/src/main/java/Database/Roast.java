package Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Database.Bean;
import Database.Checkpoint;

public class Roast implements Serializable {
    public int id;
    public String name;
    public Bean bean;
    public String roastLevel;
    public int charge_temp;
    public int dropTemp;
    public String flavour;
    public List<Checkpoint> checkpoints;

    public Roast(){
        id=0;
        name="";
        bean=null;
        roastLevel="";
        charge_temp = 0;
        dropTemp=0;
        flavour="";
        checkpoints = new ArrayList<Checkpoint>();
    }
}
