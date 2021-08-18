package Database;

import java.util.HashMap;



public class Roaster extends DbData{
    public int id=0;
    public String name;

    public Roaster(){
        id=0;
        name = "";
    }



    @Override
    public HashMap<String, String> toMap() {
        //TODO if needed for server
        return null;
    }
}
