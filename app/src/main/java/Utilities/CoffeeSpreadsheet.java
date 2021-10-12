package Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.MainActivity;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.library.worksheet.cellstyles.WorkSheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;

import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;
import Database.RoastRecord;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import static android.provider.CalendarContract.CalendarCache.URI;

public class CoffeeSpreadsheet {
    private static final int REQUEST = 112;
    private Activity activity;
    private File file;

    public CoffeeSpreadsheet(Activity activity, ArrayList<RoastRecord> records){
        this.activity = activity;
        createSpreadsheet(activity, records);
    }

    public void createSpreadsheet(Activity activity, ArrayList<RoastRecord> records){
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Sheet No 1");
        for(int i=0; i<records.size(); i++) {
            HSSFRow rowA = firstSheet.createRow(i);
            RoastRecord record = records.get(i);

            int colPos = 0;

            HSSFCell cellA = rowA.createCell(colPos++);
            cellA.setCellValue(new HSSFRichTextString(record.name));
            HSSFCell cellB = rowA.createCell(colPos++);
            cellB.setCellValue(new HSSFRichTextString(record.dateTime));

            ArrayList<Integer> temps = new ArrayList<>();
            ArrayList<Integer> checks = new ArrayList<>();
            DataSaver.loadRoastData(record, temps, checks, activity);

            for(int j=0; j<record.roastProfile.checkpoints.size(); j++){
                Checkpoint checkpoint = record.roastProfile.checkpoints.get(j);
                HSSFCell cell = rowA.createCell(colPos++);
                cell.setCellValue(new HSSFRichTextString(checkpoint.name + " \n\t" + "Time: " + CommonFunctions.secondsToTimeString(checks.get(j))+ " \n\t" + "Temp: " + checkpoint.temperature));
            }

            HSSFCell cellC = rowA.createCell(colPos++);
            cellC.setCellValue(new HSSFRichTextString("Start weight: "+record.startWeightPounds));
            HSSFCell cellD = rowA.createCell(colPos++);
            cellD.setCellValue(new HSSFRichTextString("End weight: "+record.endWeightPounds));
        }
        FileOutputStream fos = null;
        try {
            String str_path = activity.getFilesDir().getPath()+"/sheets";
            file = new File(str_path, "Spreadsheet.xls");
            file.delete();
            if(!file.exists())
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(activity, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSpreadsheet(){
        if(file==null)
            return;

        Uri contentUri = FileProvider.getUriForFile(activity, "com.example.roastingassistant.fileprovider", file);

        Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setStream(contentUri)
                .setType("application/xls")
                .getIntent()
                .setAction(Intent.ACTION_SEND)
                .setDataAndType(contentUri, "application/xls")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(intent);
    }
}
