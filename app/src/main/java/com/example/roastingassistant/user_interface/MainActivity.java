package com.example.roastingassistant.user_interface;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.menu.MenuOnClickListener;
import com.google.android.material.tabs.TabLayout;

import Database.RoastRecord;
import Utilities.CoffeeSpreadsheet;
import Utilities.CommonFunctions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roastingassistant.user_interface.main.SectionsPagerAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Database.Bean;
import Database.DatabaseHelper;
import Database.Roast;

import Utilities.GlobalSettings;
import Utilities.DataSaver;

import static kotlin.io.ConstantsKt.DEFAULT_BUFFER_SIZE;

/**
 * Main menu of the app.
 * Contains a roast, bean, and blend fragment.
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST = 112;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DatabaseHelper.getInstance(this).addRoastertable();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        GlobalSettings settings = GlobalSettings.getSettings(this);

        DataSaver.saveSettings(settings, this);

//        ArrayList<RoastRecord> records = new ArrayList<>();
//        records = DatabaseHelper.getInstance(this).getAllRoastRecords();
//        CoffeeSpreadsheet spread = new CoffeeSpreadsheet(this, records);






        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

        /**
         * Thread meant for starting up the database so that the main thread doesn't have to potentially wait on it.
         */
        class DBThread extends Thread {
            Context context;
            DatabaseHelper db;

            public DBThread(Context context) {
                this.context = context;
            }

            public void run() {
                db = DatabaseHelper.getInstance(context);
                Bean bean = new Bean();
                bean.name = "Brazil";
                bean.id = db.addBean(bean);
                Bean bean2 = new Bean();
                bean2.name = "Colombia";
                db.addBean(bean2);
                Bean bean3 = new Bean();
                bean3.name = "Peru";
                db.addBean(bean3);
                Bean bean4 = new Bean();
                bean4.name = "Ethiopia";

                Roast roast = new Roast();
                roast.name = "Testroast";
                roast.bean = bean;
                roast.dropTemp = 459;
                db.addRoast(roast);
            }
        }
        String dsfhkj = CommonFunctions.secondsToTimeString(3245);
//        ArrayList<RoastRecord> records = DatabaseHelper.getInstance(this).getAllRoastRecords();
//        if(records!=null)
//        for(int i=0; i<records.size(); i++){
//            this.deleteFile(records.get(i).name);
//        }
//
//        //-----------------Temporary db setup------------------------------
//        deleteDatabase("coffeeDatabase");//For testing purposes, delete database before use so that there is a fresh db.


        //DBThread dbt = new DBThread(this.getApplicationContext());
        //dbt.start();



        //spread.sendSpreadsheet();

    }

    private void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try {
            String str_path = this.getFilesDir().getPath()+"/sheets";
            file = new File(str_path, "Spreadsheet.xls");
            file.delete();
            if(!file.exists())
                file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            //FileOutputStream outputStream = new FileOutputStream(file, false);
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handles result of requesting write permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST:
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)
                    Log.d("Permission", "Write permission granted.");

                break;
            case STORAGE_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)
                    Log.d("Permission", "Write permission granted.");
                break;
        }
    }

    /**
     * Starts the RoastParamActivity
     */
    public void startRoastParamActivity() {
        Intent intent = new Intent(this, RoastParamActivity.class);
        intent.putExtra("Mode", RoastParamActivity.mode.adding);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    /**
     * Starts the BeanActivity
     */
    public void startBeanViewActivity() {
        Intent intent = new Intent(this, BeanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    /**
     * Starts the bean activity for viewing a database bean entry.
     * @param id database id of the desired bean
     */
    public void startBeanViewActivity(int id) {
        Intent intent = new Intent(this, BeanActivity.class).putExtra("Id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    /**
     * Starts any activity that doesn't require parameters
     * @param c The activity
     */
    public void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    /**
     * Starts any activity with pre-setup intent
     * @param c the class to be started
     * @param extra the intent with extras for the activity to be started
     */
    public void startActivity(Class c, Intent extra) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtras(extra);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    /**
     * Get activity context for anonymous inner classes
     * @return the context
     */
    public Context getContext() {
        return this;
    }


}