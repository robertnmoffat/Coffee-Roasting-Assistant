package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.roastingassistant.R;

import java.util.ArrayList;
import java.util.List;

public class RemoteDataBrowserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_data_browser);

        layout = findViewById(R.id.databrowser_data_layout);
        addButton();
        setupSpinner();
    }


    /**
     * Connect the passed Spinner with the itemNames array
     */
    public void setupSpinner(){
        List<String> itemNames = new ArrayList<String>();
        itemNames.add("None");
        itemNames.add("Roasts");
        itemNames.add("Beans");
        itemNames.add("Blends");
        //spinner click listener
        Spinner spinner = findViewById(R.id.databrowser_filter_spinner);
        spinner.setOnItemSelectedListener(this);
        //adaptor for categories and spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, itemNames);
        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }


    public void addButton(){
        String name;
        Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getname();
                Intent intent = new Intent(getContext(), RoastParamActivity.class);
                intent.putExtra("Mode", RoastParamActivity.mode.downloading);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0); //0 for no animation
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        button.setText("Brazil Dark Test");
        button.setTextColor(getResources().getColor(R.color.lightGray));
        button.setBackgroundColor(getResources().getColor(R.color.grayBack));
        //button.setMinHeight(fortydp*2+fortydp/2);
        //button.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));

        layout.addView(button);
    }
    public String getname(){
        return "";
    }
    public Context getContext(){
        return this;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

