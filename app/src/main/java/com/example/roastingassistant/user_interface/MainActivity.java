package com.example.roastingassistant.user_interface;

import android.content.Intent;
import android.os.Bundle;

import com.example.roastingassistant.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.roastingassistant.user_interface.main.SectionsPagerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Database.Bean;
import Database.DatabaseHelper;

/**
 * Main menu of the app.
 * Contains a roast, bean, and blend fragment.
 *
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

        Spinner spinner = findViewById(R.id.menuSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        deleteDatabase("coffeeDatabase");//For testing purposes, delete database before use so that there is a fresh db.

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        Bean bean = new Bean();
        bean.name = "Brazil";
        db.addBean(bean);
        Bean bean2 = new Bean();
        bean2.name = "Colombia";
        db.addBean(bean2);
        Bean bean3 = new Bean();
        bean3.name = "Peru";
        db.addBean(bean3);
        Bean bean4 = new Bean();
        bean4.name = "Ethiopia";
        db.addBean(bean4);

        ArrayList<Bean> beans = (ArrayList<Bean>) db.getAllBeans();
        for(Bean b: beans){
            Log.d("Database", b.name);
        }
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
}