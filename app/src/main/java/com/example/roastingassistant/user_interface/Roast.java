package com.example.roastingassistant.user_interface;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
