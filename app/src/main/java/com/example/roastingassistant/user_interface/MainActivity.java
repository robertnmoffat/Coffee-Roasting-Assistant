package com.example.roastingassistant.user_interface;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.roastingassistant.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.roastingassistant.user_interface.main.SectionsPagerAdapter;

import Database.Bean;
import Database.DatabaseHelper;
import Database.Roast;

/**
 * Main menu of the app.
 * Contains a roast, bean, and blend fragment.
 *
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Spinner spinner = findViewById(R.id.menuSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        /**
         * Thread meant for starting up the database so that the main thread doesn't have to potentially wait on it.
         */
        class DBThread extends Thread{
            Context context;
            DatabaseHelper db;
            public DBThread(Context context){
                this.context = context;
            }

            public void run(){
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

        //-----------------Temporary db setup------------------------------
        //deleteDatabase("coffeeDatabase");//For testing purposes, delete database before use so that there is a fresh db.

        //DBThread dbt = new DBThread(this.getApplicationContext());
        //dbt.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Database", "MainActivity resumed.");


    }

    public void startRoastParamActivity(){
        Intent intent = new Intent(this, RoastParamActivity.class);
        intent.putExtra("Mode", RoastParamActivity.mode.adding);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0); //0 for no animation
    }
    public void startBeanViewActivity(){
        Intent intent = new Intent(this, BeanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0); //0 for no animation
    }
    public void startBeanViewActivity(int id){
        Intent intent = new Intent(this, BeanActivity.class).putExtra("Id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0); //0 for no animation
    }
    public void startBlendViewActivity(){
        Intent intent = new Intent(this, BlendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0); //0 for no animation
    }

    public void startActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0); //0 for no animation
    }

    public void startActivity(Class c, Intent extra){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtras(extra);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0); //0 for no animation
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(id==3){
            deleteDatabase("coffeeDatabase");
            Toast.makeText(this.getApplicationContext(), "Database deleted.", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}