package Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.appcompat.app.AlertDialog;

public class CommonFunctions {

    public static String secondsToTimeString(int seconds){
        int mins = seconds/60;
        int secs = seconds-(mins*60);
        String minString = String.format("%02d", mins);
        String secString = String.format("%02d", secs);
        String timeString = minString+":"+secString;
        return timeString;
    }

    public static int standardTempToMetric(int fahrenheit){
        float celsius = (fahrenheit-32)/1.8f;
        return Math.round(celsius);
    }

    public static int metricTempToStandard(int celsius){
        float fahrenheit = celsius*1.8f + 32;
        return Math.round(fahrenheit);
    }

    /**
     * Convert dp amount to equivalent pixels.
     * @param num
     * @return
     */
    public static int dp(int num, Resources r){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                num,
                r.getDisplayMetrics());
    }
}
