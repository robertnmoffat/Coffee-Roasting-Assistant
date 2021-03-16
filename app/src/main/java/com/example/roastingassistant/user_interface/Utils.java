package com.example.roastingassistant.user_interface;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {

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