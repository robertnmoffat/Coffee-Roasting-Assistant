package com.example.roastingassistant.user_interface;

import java.io.Serializable;

public class Checkpoint implements Serializable {
    public enum trig{
        Temperature,
        Time,
        TurnAround,
        PromptAtTemp //User must give input at checkpoint. Example would be first or second crack.
    }

    public String name;
    public trig trigger;
    public int temperature;
    public int minutes;
    public int seconds;
}
