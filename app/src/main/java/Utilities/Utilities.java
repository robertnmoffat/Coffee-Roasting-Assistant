package Utilities;

public class Utilities {

    public static String secondsToTimeString(int seconds){
        int mins = seconds/60;
        int secs = seconds-(mins*60);
        String minString = String.format("%02d", mins);
        String secString = String.format("%02d", secs);
        String timeString = minString+":"+secString;
        return timeString;
    }
}
