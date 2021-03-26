package Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Database.Bean;
import Database.Checkpoint;

public class Roast implements Serializable {
    public String name;
    public Bean bean;
    public String roastLevel;
    public int dropTemp;
    public List<Checkpoint> checkpoints;

    Roast(){
        name="";
        bean=null;
        roastLevel="";
        dropTemp=0;
        checkpoints = new ArrayList<Checkpoint>();
    }
}
