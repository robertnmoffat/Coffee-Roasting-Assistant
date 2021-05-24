package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.roastingassistant.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Database.Bean;
import Database.Blend;
import Database.DbData;
import Database.Roast;
import Networking.HttpClient;

public class RemoteDataBrowserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, HttpCallback {
    LinearLayout layout;
    HttpClient client;
    Spinner filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_data_browser);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        layout = findViewById(R.id.databrowser_data_layout);

        client = new HttpClient();
        client.functionToPerform = HttpClient.HttpFunction.getAllNames;
        client.setLoadedCallback(this);
        //client.layout = layout;
        client.execute();



        setupSpinner();

        Button doneButton = findViewById(R.id.databrowser_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        filterSpinner = findViewById(R.id.databrowser_filter_spinner);
        filterSpinner.setOnItemSelectedListener(this);
        //adaptor for categories and spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, itemNames);
        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(dataAdapter);
    }


    public void addButton(DbData data){
        Button button = new Button(this);
        switch (data.typeName){
            case "bean":
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), BeanActivity.class);
                        intent.putExtra("Mode", RoastParamActivity.mode.downloading);
                        intent.putExtra("serverId", data.serverId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                        overridePendingTransition(0,0); //0 for no animation
                        Log.d("Server", data.name+" ID:"+data.serverId);
                    }
                });
                break;
            case "roast":
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), RoastParamActivity.class);
                        intent.putExtra("Mode", RoastParamActivity.mode.downloading);
                        intent.putExtra("serverId", data.serverId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                        overridePendingTransition(0,0); //0 for no animation
                        Log.d("Server", data.name+" ID:"+data.serverId);
                    }
                });
                break;
            case "blend":
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), BlendActivity.class);
                        intent.putExtra("Mode", RoastParamActivity.mode.downloading);
                        intent.putExtra("serverId", data.serverId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                        overridePendingTransition(0,0); //0 for no animation
                        Log.d("Server", data.name+" ID:"+data.serverId);
                    }
                });
                break;
        }


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        String typename = data.typeName.substring(0, 1).toUpperCase() + data.typeName.substring(1);
        button.setText(typename+": "+data.name);
        button.setTextColor(getResources().getColor(R.color.lightGray));
        button.setBackgroundColor(getResources().getColor(R.color.grayBack));
        button.setId(R.id.databrowser_openRoast_button);
        //button.setMinHeight(fortydp*2+fortydp/2);
        //button.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));

        layout.addView(button);
    }
    public Context getContext(){
        return this;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(client.dbData==null)
            return;
        //TODO: all this stuff
        layout.removeAllViews();

        String filterText = filterSpinner.getSelectedItem().toString();
        filterText = filterText.toLowerCase();
        filterText = filterText.substring(0, filterText.length()-1);
        for(DbData data: client.dbData){
            if(data.typeName!=filterText){
                //((ViewGroup) layout).removeView(checkDescription);
            }
        }
        //((ViewGroup) textAndButton).removeView(checkDescription);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDataLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Collections.sort(client.dbData, new Comparator<DbData>() {
                    @Override
                    public int compare(DbData o1, DbData o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });
                for(DbData data: client.dbData){
                    addButton(data);
                }
            }
        });

    }
}

