package Utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Database.Roast;

public class DataSaver {


    public static void saveRoastData(Roast roast, ArrayList<Integer> tempsOverTime, float startWeight, float endWeight, Context context){
        Date currentTime = Calendar.getInstance().getTime();
        Log.d("saveRoastData", currentTime.toString());

        /**
         * Filestructure
         *
         * count of checkpoints
         * count of temp samples
         * loop checkpoints
         *      time short
         *      temp short
         * Loop for roast temp samples
         *      time
         *      temp
         */
        File directory = context.getFilesDir();
        File file = new File(directory, "");

    }
}
