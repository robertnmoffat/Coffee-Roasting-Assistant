public class Checkpoint {
    enum trig{
        temperature,
        time
    }

    public String name;
    public trig trigger;
    public int temperature;
    public int minutes;
    public int seconds;
}
