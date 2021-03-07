package com.example.roastingassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RoastParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_param);

        Spinner spinner = findViewById(R.id.bean_spinner);
        setupSpinner(spinner);

    }

    public void setupSpinner(Spinner spinner){
        //spinner click listener
        spinner.setOnItemSelectedListener(this);
        //set spinner drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Beans");//TODO: add stuff from database later
        //adaptor for categories and spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, categories);
        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}