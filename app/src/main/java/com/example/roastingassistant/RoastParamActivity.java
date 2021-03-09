package com.example.roastingassistant;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RoastParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int checkpointId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_param);

//        EditText ed = findViewById(R.id.roast_name_edittext);
//        ed.setText("Brazil Dark");
//        ed.setEnabled(false);

        Spinner spinner = findViewById(R.id.bean_spinner);
        //set spinner drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Beans");//TODO: add stuff from database later
        categories.add("second");
        categories.add("third");
        setupSpinner(spinner, categories);

        Spinner checkPointSpinner = findViewById(R.id.checkpoint_spinner);
        //set spinner drop down elements
        categories = new ArrayList<String>();
        categories.add("New "+getString(R.string.checkpoint_text));//TODO: add stuff from database later
        setupSpinner(checkPointSpinner, categories);

        Context context = this;

        Button roastAddButton = findViewById(R.id.roast_add_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        Button addCheckpointButton = findViewById(R.id.add_checkpoint_button);
        addCheckpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(checkpointId){
                    case 0:
                        Intent intent = new Intent(context, CheckpointParamActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                        overridePendingTransition(0,0); //0 for no animation
                        break;

                }

            }
        });
    }

    public void setupSpinner(Spinner spinner, List<String> itemNames){
        //spinner click listener
        spinner.setOnItemSelectedListener(this);
        //adaptor for categories and spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, itemNames);
        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = parent.getId()==R.id.bean_spinner? "Bean":"Checkpoint";
        Log.i("Spinner", "ID: "+id+" View: "+name);

        if(parent.getId()==R.id.bean_spinner){
            switch((int)id){
                case 0:
                    Log.i("Spinner", "Bean selected.");
                    break;
            }
        }else{
            checkpointId = (int)id;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("Spinner", "nothing selected");
    }
}