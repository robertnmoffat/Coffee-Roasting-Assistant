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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.menu.MenuOnClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Database.Bean;
import Database.Blend;
import Database.DbData;
import Database.Roast;
import Networking.HttpClient;

/**
 * Activity for viewing data stored on the server
 */
public class RemoteDataBrowserActivity extends AppCompatActivity implements HttpCallback {
    LinearLayout layout;
    HttpClient client;
    Spinner filterSpinner;

    ArrayList<Button> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_data_browser);

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

        buttons = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        String viewType = intent.getStringExtra("ViewType");//Which type of data is being browsed

        layout = findViewById(R.id.databrowser_data_layout);

        client = new HttpClient();
        client.functionToPerform = HttpClient.HttpFunction.getAllNames;
        switch (viewType){
            case "Roast":
                client.functionToPerform = HttpClient.HttpFunction.getAllRoastNames;
                break;
            case "Bean":
                client.functionToPerform = HttpClient.HttpFunction.getAllBeanNames;
                break;
            case "Blend":
                client.functionToPerform = HttpClient.HttpFunction.getAllBlendNames;
                break;

        }
        client.setLoadedCallback(this);
        client.execute();

        EditText searchText = findViewById(R.id.databrowser_search_edittext);
        Button searchButton = findViewById(R.id.databrowser_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyButtonFilter(searchText.getText().toString().toLowerCase());
            }
        });

        Button doneButton = findViewById(R.id.databrowser_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Clears all data buttons. Used before applying a search filter
     */
    public void clearButtons(){
        for (Button button:
             buttons) {
            layout.removeView(button);
        }
    }

    /**
     * Only add buttons that contain a search string. Not case sensitive.
     * @param string Search string
     */
    public void applyButtonFilter(String string){
        clearButtons();

        for(DbData data: client.dbData){
            if(data.name.toLowerCase().contains(string))
                addButton(data);
        }
    }

    /**
     * Adds a button representing a server data table
     * @param data
     */
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
        buttons.add(button);
    }

    /**
     * get activity context for anonymous inner classes
     * @return
     */
    public Context getContext(){
        return this;
    }

    /**
     * Called when the data has been downloaded from the server.
     * Updates UI based on the downloaded data.
     */
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

