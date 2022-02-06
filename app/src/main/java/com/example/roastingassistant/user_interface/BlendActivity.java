package com.example.roastingassistant.user_interface;

import Utilities.CommonFunctions;
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
import com.example.roastingassistant.user_interface.menu.MenuOnClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Database.Blend;
import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;
import Networking.HttpClient;

/**
 * Activity for displaying and editing different coffee blend information.
 * Main functionality is to tie different roasts together.
 */
public class BlendActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, HttpCallback {
    ArrayList<Roast> roasts;
    ArrayList<Roast> roastsAdded;
    int roastSelected = 0;

    boolean viewing = false;//if viewing a previously saved blend rather than creating a new one

    HttpClient client;
    int serverId=0;

    /**
     * Sets up the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blend);

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        roasts = db.getAllRoasts();
        roastsAdded = new ArrayList<Roast>();

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

        Intent intent = getIntent();
        int id = intent.getIntExtra("Id", -1);
        if (id != -1 && id != 0) {
            viewing = true;
            Blend blend = db.getBlend(id);
            setupForViewing(blend);
        }
        serverId = intent.getIntExtra("serverId", -1);
        if (serverId != -1 && serverId != 0) {
            setupForDownloading(serverId);
        }

        Button roastAddButton = findViewById(R.id.blend_addroast_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roastSelected == 0) {
                    Toast.makeText(getContext().getApplicationContext(), "Roast type must be selected.", Toast.LENGTH_SHORT).show();
                    return;
                }
                roastsAdded.add(roasts.get(roastSelected - 1));
                createRoastUI(roasts.get(roastSelected - 1));
            }
        });

        Button saveButton = findViewById(R.id.blend_add_button);
        if (viewing) {
            if (serverId > 0)
                saveButton.setText("Download");
            else
                saveButton.setText("Done");
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewing) {
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
        for (Roast roast : roasts) {
            if (roast.name.contains(" "))
                categories.add(roast.name);
            else
                categories.add(roast.name + ": " + roast.roastLevel + " " + roast.dropTemp);
        }

        setupSpinner(spinner, categories);
    }

    /**
     * Download blend entry
     * @param serverId ID of he blend on the server
     */
    public void setupForDownloading(int serverId) {
        client = new HttpClient();
        client.idToGet = serverId;
        client.functionToPerform = HttpClient.HttpFunction.getBlend;
        client.setLoadedCallback(this);
        client.execute();
    }

    /**
     * Transfers Blend object information to UI
     * @param blend the blend to be loaded into the ui
     */
    public void setupForViewing(Blend blend) {
        EditText nameET = findViewById(R.id.blend_name_edittext);
        nameET.setText(blend.name);
        nameET.setEnabled(false);
        EditText descriptionET = findViewById(R.id.blend_description_edittext);
        descriptionET.setText(blend.description);
        descriptionET.setEnabled(false);
        if (blend.roasts != null) {
            for (Roast roast : blend.roasts) {
                createRoastUI(roast);
            }
        }
        findViewById(R.id.blend_addroast_button).setVisibility(View.GONE);
        findViewById(R.id.roast_for_blend_spinner).setVisibility(View.GONE);
    }

    /**
     * Connects a Spinner View with the List of names to attach to it's dropdown menu.
     *
     * @param spinner the spinner
     * @param itemNames list of names contained in the spinner element
     */
    public void setupSpinner(Spinner spinner, List<String> itemNames) {
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
     *
     * @param parent ignored
     * @param view ignored
     * @param position ignored
     * @param id id of the selected roast
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        roastSelected = (int) id;
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Adds a selected roast to the UI. This includes remove button and name/description.
     * @param roast Roast to add to UI
     */
    public void createRoastUI(Roast roast) {
        String name = roast.name;
        String description = roast.roastLevel + " " + roast.dropTemp;
        int dbId = roast.id;

        LinearLayout roastLayout = findViewById(R.id.blend_roast_layout);
        TextView checkDescription = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, CommonFunctions.dp(10, getResources()), CommonFunctions.dp(40, getResources()), CommonFunctions.dp(10, getResources()));
        params.gravity = Gravity.RIGHT;
        checkDescription.setLayoutParams(params);
        checkDescription.setText(name + " " + description);
        checkDescription.setTextColor(getResources().getColor(R.color.lightGray));
        checkDescription.setBackgroundColor(getResources().getColor(R.color.grayBack));
        checkDescription.setTextSize(CommonFunctions.dp(7, getResources()));
        checkDescription.setGravity(Gravity.RIGHT);

        LinearLayout textAndButton = new LinearLayout(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(0, CommonFunctions.dp(10, this.getResources()), 0, 0);
        textAndButton.setLayoutParams(params2);
        textAndButton.setOrientation(LinearLayout.HORIZONTAL);
        int id = ViewCompat.generateViewId();//TODO:Store id in array
        textAndButton.setId(id);//set to generated id

        Button removeButton = new Button(this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params3.setMargins(CommonFunctions.dp(20, this.getResources()), 0, 0, 0);
        removeButton.setLayoutParams(params3);
        removeButton.setMaxWidth(CommonFunctions.dp(10, getResources()));
        removeButton.setMaxHeight(CommonFunctions.dp(10, getResources()));
        removeButton.setText("-");
        removeButton.setTextColor(getResources().getColor(R.color.white));
        removeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
        removeButton.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));
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

    /**
     * Copy blend information from the UI into a new blend Blend object.
     * @return New Blend object containing UI information.
     */
    public Blend getBlendFromActivity() {
        Blend blend = new Blend();
        blend.name = ((EditText) findViewById(R.id.blend_name_edittext)).getText().toString();
        blend.description = ((EditText) findViewById(R.id.blend_description_edittext)).getText().toString();
        blend.roasts = new ArrayList<>(roastsAdded);

        return blend;
    }

    /**
     * Get the context of this activity. (For inner anonymous classes to reference)
     * @return the context
     */
    public Context getContext() {
        return this;
    }

    /**
     * Handles filling in UI elements once data has been downloaded from the server.
     */
    @Override
    public void onDataLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupForViewing(client.blend);

                Button saveButton = findViewById(R.id.blend_add_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
                        for(Roast roast: client.blend.roasts){
                            for(Checkpoint checkpoint: roast.checkpoints){
                                int checkId = db.addCheckpoint(checkpoint);
                                checkpoint.id = checkId;
                            }
                            int beanId = db.addBean(roast.bean);
                            roast.bean.id = beanId;
                            int roastId = db.addRoast(roast);
                            roast.id = roastId;
                        }
                        int blendId = db.addBlend(client.blend);

                        finish();
                    }
                });
            }
        });
    }
}