package Database;

import android.widget.TextView;

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

    public Checkpoint(){
        name="";
        trigger=trig.Temperature;
        temperature=0;
        minutes=0;
        seconds=0;
    }
}
