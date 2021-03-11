package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.roastingassistant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying and handling alterations on different coffee blend information.
 * Main functionality is to add different roasts and the percentage the blend is made up of each roast.
 */
public class BlendActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blend);

        Button roastAddButton = findViewById(R.id.blend_add_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner spinner = findViewById(R.id.roast_for_blend_spinner);
        Spinner checkPointSpinner = findViewById(R.id.checkpoint_spinner);
        List<String> categories = new ArrayList<String>();
        categories.add("Roast");//TODO: add stuff from database later
        setupSpinner(spinner, categories);
    }

    /**
     * Connects a Spinner View with the List of names to attach to it's dropdown menu.
     * @param spinner
     * @param itemNames
     */
    public void setupSpinner(Spinner spinner, List<String> itemNames){
        //spinner click listener
        spinner.setOnItemSelectedListener(this);
        //adaptor for categories and spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, itemNames);
        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    /**
     * Called when an item has been selected from the dropdown menu.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}