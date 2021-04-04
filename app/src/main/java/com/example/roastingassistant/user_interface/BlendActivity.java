package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roastingassistant.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Database.Blend;
import Database.DatabaseHelper;
import Database.Roast;

/**
 * Activity for displaying and handling alterations on different coffee blend information.
 * Main functionality is to add different roasts and the percentage the blend is made up of each roast.
 */
public class BlendActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<Roast> roasts;
    ArrayList<Roast> roastsAdded;
    int roastSelected=0;

    boolean viewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blend);

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        roasts = db.getAllRoasts();
        roastsAdded = new ArrayList<Roast>();

        Intent intent = getIntent();
        int id = intent.getIntExtra("Id",-1);
        if(id!=-1&&id!=0){
            viewing = true;
            Blend blend = db.getBlend(id);
            setupForViewing(blend);
        }

        Button roastAddButton = findViewById(R.id.blend_addroast_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roastSelected==0){
                    Toast.makeText(getContext().getApplicationContext(), "Roast type must be selected.", Toast.LENGTH_SHORT).show();
                    return;
                }
                roastsAdded.add(roasts.get(roastSelected-1));
                createRoastUI(roasts.get(roastSelected-1));
            }
        });

        Button saveButton = findViewById(R.id.blend_add_button);
        if(viewing)
            saveButton.setText("Done");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewing) {
                    Blend blend = getBlendFromActivity();
                    int blendId = db.addBlend(blend);
                }

                finish();
            }
        });

        Spinner spinner = findViewById(R.id.roast_for_blend_spinner);
        Spinner checkPointSpinner = findViewById(R.id.roastparamactivity_checkpoint_spinner);
        List<String> categories = new ArrayList<String>();
        categories.add("Roast");
        for(Roast roast: roasts){
            if(roast.name.contains(" "))
                categories.add(roast.name);
            else
                categories.add(roast.name+": "+roast.roastLevel+" "+roast.dropTemp);
        }

        setupSpinner(spinner, categories);
    }

    public void setupForViewing(Blend blend){
        EditText nameET = findViewById(R.id.blend_name_edittext);
        nameET.setText(blend.name);
        EditText descriptionET = findViewById(R.id.blend_description_edittext);
        descriptionET.setText(blend.description);
        if(blend.roasts!=null) {
            for (Roast roast : blend.roasts) {
                createRoastUI(roast);
            }
        }
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
        roastSelected=(int)id;
    }

    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void createRoastUI(Roast roast){
        String name = roast.name;
        String description = roast.roastLevel+" "+roast.dropTemp;
        int dbId = roast.id;

        LinearLayout roastLayout = findViewById(R.id.blend_roast_layout);
        TextView checkDescription = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,Utils.dp(10,getResources()), Utils.dp(40,getResources()), Utils.dp(10,getResources()));
        params.gravity = Gravity.RIGHT;
        checkDescription.setLayoutParams(params);
        checkDescription.setText(name+" "+description);
        checkDescription.setTextColor(getResources().getColor(R.color.lightGray));
        checkDescription.setBackgroundColor(getResources().getColor(R.color.grayBack));
        checkDescription.setTextSize(Utils.dp(7, getResources()));
        checkDescription.setGravity(Gravity.RIGHT);

        LinearLayout textAndButton = new LinearLayout(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(0, Utils.dp(10, this.getResources()), 0, 0);
        //params2.gravity = Gravity.CENTER;
        textAndButton.setLayoutParams(params2);
        textAndButton.setOrientation(LinearLayout.HORIZONTAL);
        int id = ViewCompat.generateViewId();//TODO:Store id in array
        textAndButton.setId(id);//set to generated id
        //textAndButton.setGravity(Gravity.CENTER);

        Button removeButton = new Button(this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params3.gravity = Gravity.CENTER;
        params3.setMargins(Utils.dp(20,this.getResources()),0,0,0);
        removeButton.setLayoutParams(params3);
        removeButton.setMaxWidth(Utils.dp(10, getResources()));
        removeButton.setMaxHeight(Utils.dp(10, getResources()));
        removeButton.setText("-");
        removeButton.setTextColor(getResources().getColor(R.color.white));
        removeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
        removeButton.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));
        //removeButton.setGravity(Gravity.CENTER);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Remove checkpoint button
                roastsAdded.remove(dbId - 1);
                ((ViewGroup) textAndButton).removeView(checkDescription);
                ((ViewGroup) textAndButton).removeView(removeButton);
                ((ViewGroup) roastLayout).removeView(textAndButton);
            }
        });

        textAndButton.addView(removeButton);
        textAndButton.addView(checkDescription);
        roastLayout.addView(textAndButton);

    }

    public Blend getBlendFromActivity(){
        Blend blend = new Blend();
        blend.name = ((EditText)findViewById(R.id.blend_name_edittext)).getText().toString();
        blend.description = ((EditText)findViewById(R.id.blend_description_edittext)).getText().toString();
        blend.roasts = new ArrayList<>(roastsAdded);

        return blend;
    }

    public Context getContext(){
        return this;
    }
}