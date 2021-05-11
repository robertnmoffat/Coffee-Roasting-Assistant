package Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database.Bean;
import Database.Checkpoint;

public class Roast extends DbData implements Serializable {
    public int id;
    public String name;
    public Bean bean;
    public String roastLevel;
    public int chargeTemp;
    public int dropTemp;
    public String flavour;
    public List<Checkpoint> checkpoints;

    public Roast(){
        id=0;
        name="";
        bean=null;
        roastLevel="";
        chargeTemp = 0;
        dropTemp=0;
        flavour="";
        checkpoints = new ArrayList<Checkpoint>();
    }

    @Override
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("bean", ""+bean.serverId);
        map.put("roastLevel", roastLevel);
        map.put("chargeTemp", ""+chargeTemp);
        map.put("dropTemp", ""+dropTemp);
        map.put("flavour", flavour);
        StringBuilder sb = new StringBuilder();
        for(Checkpoint check : checkpoints) {
            sb.append(check.toString());
        }
        //map.put("checkpoints" , sb.toString());

        return map;
    }
}
