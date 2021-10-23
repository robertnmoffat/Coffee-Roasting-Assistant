package Utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import Database.Roast;
import Database.RoastRecord;

public class DataSaver {

    public static int convertBytesToInt(byte[] bytes, int startPos) {
        byte[] fBytes = new byte[]{bytes[startPos+0], bytes[startPos+1], bytes[startPos+2], bytes[startPos+3]};
        return ByteBuffer.wrap(fBytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public static void loadRoastData(RoastRecord record, ArrayList<Integer> tempsToLoad, ArrayList<Integer> checkpointsToLoad, Context context){
        if(record==null||tempsToLoad==null||checkpointsToLoad==null||context==null) {
            Log.e("FileIO", "Parameter error loading roast data.");
            return;
        }
        try {
            //FileInputStream fis = context.openFileInput(record.filename);
            InputStream is = context.openFileInput(record.filename);
            byte[] bytes = new byte[record.filesizeBytes];
            Log.d("FileIO", ""+is.read(bytes));
            int byteOffset=0;
            int checkCount = convertBytesToInt(bytes, 0);
            int tempCount = convertBytesToInt(bytes, 4);
            byteOffset+=8;
            for(int i=0; i<checkCount; i++){
                checkpointsToLoad.add(convertBytesToInt(bytes, byteOffset));
                byteOffset+=4;
            }
            for(int i=0; i<tempCount; i++){
                tempsToLoad.add(convertBytesToInt(bytes, byteOffset));
                byteOffset+=4;
            }


//            Scanner scan = new Scanner(is);
//            if(scan.hasNextInt()) {
//                int checkLength = scan.nextInt();
//                int tempLength = scan.nextInt();
//                for (int i = 0; i < checkLength; i++) {
//                    checkpointsToLoad.add(scan.nextInt());
//                }
//                for (int i = 0; i < tempLength; i++) {
//                    tempsToLoad.add(scan.nextInt());
//                }
//                Log.d("FileIO", "File successfully read.");
//            }
//            scan.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRoastData(RoastRecord record, ArrayList<Integer> tempsOverTime, ArrayList<Integer> checkpointTimes, Context context){
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

        File file = new File(context.getFilesDir(), record.filename);
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            //FileOutputStream fos = context.openFileOutput(record.filename, context.MODE_PRIVATE);
            //Count of checkpoints
            dos.writeInt(checkpointTimes.size());
            //count of temp samples
            dos.writeInt(tempsOverTime.size());
            //loop checkpoints
            for(int i=0; i<checkpointTimes.size(); i++){
                dos.writeInt(checkpointTimes.get(i));
            }
            for(int i=0; i<tempsOverTime.size(); i++){
                dos.writeInt(tempsOverTime.get(i));
            }
            dos.close();

        } catch (FileNotFoundException e) {
            Log.e("FileIO", "File not found exception thrown.");
            e.printStackTrace();
        } catch (IOException e){

        } finally {

        }
    }

    public static void saveSettings(GlobalSettings settings, Context context){
        File file = new File(context.getFilesDir(),"Settings");
        try {
            FileOutputStream os = new FileOutputStream(file);
            PrintWriter writer = new PrintWriter(os);
            writer.println("metric:"+settings.isMetric());
            writer.println("username:"+settings.getUsername());
            writer.println("language:"+settings.getLanguage().toString());
            writer.println("brightness:"+settings.getCameraBrightness());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void loadSettings(GlobalSettings settings, Context context){
        File file = new File(context.getFilesDir(),"Settings");
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            String metric = reader.readLine().split(":")[1];
            String username = reader.readLine().split(":")[1];
            String language = reader.readLine().split(":")[1];
            String brightness = reader.readLine().split(":")[1];

            settings.setMetric(metric.equals("true")?true:false, context);
            settings.setUsername(username, context);
            settings.setLanguage(GlobalSettings.Language.valueOf(language), context);
            settings.setCameraBrightness(Float.parseFloat(brightness), context);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
