package com.example.roastingassistant.user_interface;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.roastingassistant.R;
import com.google.android.material.tabs.TabLayout;

import Utilities.CommonFunctions;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roastingassistant.user_interface.main.SectionsPagerAdapter;

import Database.Bean;
import Database.DatabaseHelper;
import Database.Roast;

import Utilities.GlobalSettings;
import Utilities.DataSaver;

/**
 * Main menu of the app.
 * Contains a roast, bean, and blend fragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        GlobalSettings settings = GlobalSettings.getSettings(this);
        DataSaver.saveSettings(settings, this);

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                MainActivity main = (MainActivity) getActivity();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.settingsmenu_units:
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                GlobalSettings.getSettings(getContext()).setMetric(true, getContext());
                                                Toast.makeText(getContext(), "Set to Metric.", Toast.LENGTH_SHORT).show();
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                GlobalSettings.getSettings(getContext()).setMetric(false, getContext());
                                                Toast.makeText(getContext(), "Set to Standard.", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Which unit to use?").setPositiveButton("Metric", dialogClickListener)
                                        .setNegativeButton("Standard", dialogClickListener).show();
                                break;
                            case R.id.settingsmenu_language:
                                Toast.makeText(getContext(), "Only English is currently supported.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.settingsmenu_username:
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                                alert.setTitle("Username");
                                //alert.setMessage("Message");

                                // Set an EditText view to get user input
                                final EditText input = new EditText(getContext());
                                alert.setView(input);

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        GlobalSettings.getSettings(getContext()).setUsername(input.getText().toString(), getContext());
                                    }
                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });

                                alert.show();
                                break;
                            case R.id.settingsmenu_delDb:
                                DialogInterface.OnClickListener delDialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                deleteDatabase("coffeeDatabase");
                                                Toast.makeText(getContext(), "Database deleted.", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(getIntent());
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder delBuilder = new AlertDialog.Builder(getContext());
                                delBuilder.setMessage("Are you sure you wish to clear local database?").setPositiveButton("Yes", delDialogClickListener)
                                        .setNegativeButton("No", delDialogClickListener).show();
                                break;
                        }

                        return false;//whether to consume the click message or send on to others
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.settings_menu, popup.getMenu());
                popup.show();
            }
        });

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Database", "MainActivity resumed.");


    }

    public Context getActivity() {
        return this;
    }

    public void startRoastParamActivity() {
        Intent intent = new Intent(this, RoastParamActivity.class);
        intent.putExtra("Mode", RoastParamActivity.mode.adding);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    public void startBeanViewActivity() {
        Intent intent = new Intent(this, BeanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    public void startBeanViewActivity(int id) {
        Intent intent = new Intent(this, BeanActivity.class).putExtra("Id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    public void startBlendViewActivity() {
        Intent intent = new Intent(this, BlendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    public void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    public void startActivity(Class c, Intent extra) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtras(extra);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0); //0 for no animation
    }

    public Context getContext() {
        return this;
    }


}