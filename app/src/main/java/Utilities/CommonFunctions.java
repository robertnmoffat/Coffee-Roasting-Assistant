package Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.TypedValue;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

public class CommonFunctions {

    public static String secondsToTimeString(int seconds){
        int mins = seconds/60;
        int secs = seconds-(mins*60);
        String minString = String.format("%02d", mins);
        String secString = String.format("%02d", secs);
        String timeString = minString+":"+secString;
        return timeString;
    }

    public static float poundsToKg(float pounds){
        return pounds*0.45359237f;
    }

    public static float KgToPounds(float Kg){
        return Kg/0.45359237f;
    }

    public static int standardTempToMetric(int fahrenheit){
        float celsius = (fahrenheit-32)/1.8f;
        return Math.round(celsius);
    }

    public static int metricTempToStandard(int celsius){
        float fahrenheit = celsius*1.8f + 32;
        return Math.round(fahrenheit);
    }

    public static String formatTempString(int temp, Context context){
        boolean isMetric = GlobalSettings.getSettings(context).isMetric();
        temp = isMetric?standardTempToMetric(temp):temp;
        String tempString = ""+temp+(isMetric?'C':'F');
        return tempString;
    }

    public static String formatWeightString(float weight, Context context){
        boolean isMetric = GlobalSettings.getSettings(context).isMetric();
        weight = isMetric?poundsToKg(weight):weight;
        return String.format("%.2f", weight)+(isMetric?"Kg":"Lbs");
    }

    public static String formatWeightStringNumber(float weight, Context context){
        boolean isMetric = GlobalSettings.getSettings(context).isMetric();
        weight = isMetric?poundsToKg(weight):weight;
        return String.format("%.2f", weight);
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
