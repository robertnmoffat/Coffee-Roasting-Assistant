package com.example.roastingassistant.user_interface;

import Database.DbData;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import Database.Bean;
import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;
import Networking.HttpClient;

/**
 * Activity for displaying and handling alterations on different roasts roasting parameters.
 */
public class RoastParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, HttpCallback {
    public enum mode{
        adding,
        viewing,
        downloading
    }

    HttpClient client;

    public static final int CHECKPOINT_REQUEST = 1;

    int checkpointId = 0;
    mode curMode = mode.adding;
    Context context;

    int beanSpinnerSelection = 0;

    //Models from database
    ArrayList<Bean> beans;
    ArrayList<Checkpoint> checkpoints;
    ArrayList<Checkpoint> checkpointsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_param);

        context = this;
        checkpointsAdded = new ArrayList<Checkpoint>();
        checkpoints = new ArrayList<Checkpoint>();

        getMode(savedInstanceState);//set the mode of the activity

        setupAndLoadSpinners();

        switch (curMode){
            case adding:
                //Default layout
                break;
            case viewing:
                Intent intent = getIntent();
                int id = intent.getIntExtra("Id",-1);
                DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
                Roast roast = db.getRoast(id);

                setupViewMode(true, roast);
                break;

            case downloading:
                setupDownloadMode();
                break;
        }

        setupCheckpoints();

        //---------Handle clicks on the button for adding the roast parameters to a new roast entry in the database.
        Button roastAddButton = findViewById(R.id.roastparamactivity_add_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curMode==mode.adding){
                    Log.d("Database", "bean selection:"+beanSpinnerSelection);
                    String nameText = ((EditText)(findViewById(R.id.roastparamactivity_name_edittext))).getText().toString();
                    if(nameText.equals("")){
                        Toast.makeText(getContext().getApplicationContext(), "Roast name cannot be blank.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(beanSpinnerSelection==0){
                        Toast.makeText(getContext().getApplicationContext(), "Bean type must be selected.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Roast thisRoast = getRoast();

                    DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
                    db.addRoast(thisRoast);
                }

                finish();
            }
        });


    }

    public Roast getRoast(){
        Roast roast = new Roast();
        roast.name = ((EditText)findViewById(R.id.roastparamactivity_name_edittext)).getText().toString();
        roast.bean = beans.get(beanSpinnerSelection-1);
        roast.roastLevel = ((EditText)findViewById(R.id.roastparamactivity_roastlevel_edittext)).getText().toString();
        //TODO: roast.charge_temp
        String dropText = ((EditText)findViewById(R.id.roastparamactivity_droptemp_edittext)).getText().toString();
        roast.dropTemp = dropText.equals("")?0 : Integer.parseInt(dropText);
        //TODO: roast.flavour
        roast.checkpoints = new ArrayList<>(checkpointsAdded);

        return roast;
    }

    /**
     * Get the mode which the activity is supposed to be in.
     * Modes are either adding, viewing, or downloading.
     * @param savedInstanceState
     */
    public void getMode(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                curMode= mode.adding;
            } else {
                curMode= (mode)extras.get("Mode");
            }
        } else {
            curMode = (mode) savedInstanceState.getSerializable("Mode");
        }
    }

    /**
     * Adds a checkpoint display for the user.
     * @param name
     * @param description
     */
    public void createCheckPoint(String name, String description, int dbId){
        LinearLayout checkLayout = findViewById(R.id.roastparamactivity_checkpoints_layout);
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
        textAndButton.setLayoutParams(params2);
        textAndButton.setOrientation(LinearLayout.HORIZONTAL);
        int id = ViewCompat.generateViewId();//TODO:Store id in array
        textAndButton.setId(id);//set to generated id

        if(curMode==mode.adding) {
            Button removeButton = new Button(this);
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params3.setMargins(Utils.dp(20,this.getResources()),0,0,0);
            removeButton.setLayoutParams(params3);
            removeButton.setMaxWidth(Utils.dp(10, getResources()));
            removeButton.setMaxHeight(Utils.dp(10, getResources()));
            removeButton.setText("-");
            removeButton.setTextColor(getResources().getColor(R.color.white));
            removeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
            removeButton.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//Remove checkpoint button
                    checkpointsAdded.remove(dbId-1);
                    ((ViewGroup)textAndButton).removeView(checkDescription);
                    ((ViewGroup)textAndButton).removeView(removeButton);
                }
            });

            textAndButton.addView(removeButton);
        }

        textAndButton.addView(checkDescription);
        checkLayout.addView(textAndButton);
    }

    public void setupCheckpoints(){
        //-------Handle clicks on the button for adding new roast checkpoints to the current roast.
        Button addCheckpointButton = findViewById(R.id.roastparamactivity_add_checkpoint_button);
        addCheckpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkpointId==0){
                    Intent intent = new Intent(context, CheckpointParamActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, CHECKPOINT_REQUEST);
                    overridePendingTransition(0,0); //0 for no animation

                }else{
                    Checkpoint checkpoint = checkpoints.get(checkpointId-1);
                    addCheckPoint(checkpoint);
                }

            }
        });
    }

    public void setupAndLoadSpinners(){
        Spinner spinner = findViewById(R.id.roastparamactivity_bean_spinner);
        //set spinner drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Beans");

        //Get beans from database
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        beans = (ArrayList<Bean>) db.getAllBeans();
        for(Bean bean: beans) {
            categories.add(bean.name);
        }
        setupSpinner(spinner, categories);

        Spinner checkPointSpinner = findViewById(R.id.roastparamactivity_checkpoint_spinner);
        //set spinner drop down elements
        categories = new ArrayList<String>();
        categories.add("New " + getString(R.string.checkpoint_text));//TODO: add stuff from database later

        //Get checkpoints from Database
        checkpoints = (ArrayList<Checkpoint>) db.getAllCheckpoints();
        for(Checkpoint checkpoint: checkpoints) {
            categories.add(checkpoint.name);
        }
        setupSpinner(checkPointSpinner, categories);
    }

    /**
     * Sets up the activity for viewing already existing roasts
     * @param startButton Whether or not to display the button to start the roast.
     */
    public void setupViewMode(boolean startButton, Roast roast){
        String roastString = roast.toString();
        EditText nameEd = findViewById(R.id.roastparamactivity_name_edittext);
        nameEd.setText(roast.name);
        nameEd.setEnabled(false);

        Spinner beanSpinner = findViewById(R.id.roastparamactivity_bean_spinner);
        beanSpinner.setSelection(roast.bean.id);
        beanSpinner.setEnabled(false);
        beanSpinner.setClickable(false);

        EditText lvlEd = findViewById(R.id.roastparamactivity_roastlevel_edittext);
        lvlEd.setText(roast.roastLevel);
        lvlEd.setEnabled(false);

        EditText dropEd = findViewById(R.id.roastparamactivity_droptemp_edittext);
        dropEd.setText(""+roast.dropTemp);
        dropEd.setEnabled(false);

        Button downloadButton = findViewById(R.id.roastparamactivity_add_button);
        downloadButton.setText("Done");

        for (Checkpoint check: roast.checkpoints) {
            addCheckPoint(check);
        }

        Button addCheck = findViewById(R.id.roastparamactivity_add_checkpoint_button);
        addCheck.setVisibility(View.GONE);
        Spinner checkSpin = findViewById(R.id.roastparamactivity_checkpoint_spinner);
        checkSpin.setVisibility(View.GONE);

        if(startButton){
            Button startRoastButton = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //convert 40dp to equivalent pixels.

            //--------Converting sizes to dp----------
            int fortydp = Utils.dp(40, getResources());
            int tendp = Utils.dp(10, getResources());

            params.setMargins(fortydp,0,0,0);
            startRoastButton.setLayoutParams(params);
            startRoastButton.setText("Roast");
            startRoastButton.setTextColor(getResources().getColor(R.color.white));
            startRoastButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
            startRoastButton.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));
            startRoastButton.setGravity(Gravity.CENTER);
            startRoastButton.setId(R.id.roastparamactivity_startroast_button);

            startRoastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:open roast activity
                    Intent intent = new Intent(context, RoastActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("RoastId", roast.id);
                    //intent.putExtras(extra);
                    //startActivityForResult(intent, 0);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(0,0); //0 for no animation
                }
            });

            LinearLayout ll = findViewById(R.id.roastparamactivity_buttonsarea_layout);
            ll.addView(startRoastButton);
        }
    }

    /**
     * Sets up the activity for viewing downloadable roasts from the server
     */
    public void setupDownloadMode(){
        client = new HttpClient();

        Intent intent = getIntent();
        int id = intent.getIntExtra("serverId",-1);

        Button downloadButton = findViewById(R.id.roastparamactivity_add_button);
        downloadButton.setText("Download");

        client.idToGet = id;
        client.functionToPerform = HttpClient.HttpFunction.getRoast;
        client.setLoadedCallback(this);
        client.execute();
    }

    /**
     * Connect the passed Spinner with the itemNames array
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
     * Called when an item on either of the Spinner Views are selected. Checks parent id to find which spinner it was, and then handles the given selection.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = parent.getId()==R.id.roastparamactivity_bean_spinner? "Bean":"Checkpoint";
        Log.i("Spinner", "ID: "+id+" View: "+name);

        if(name.equals("Bean")){
            beanSpinnerSelection = (int)id;

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


    /**
     * Method called after the CheckpointParamActivity returns.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHECKPOINT_REQUEST){
            if(resultCode==RESULT_OK) {
                Log.i("callback", "Callback received!");
                Checkpoint checkpoint = (Checkpoint) (Checkpoint)data.getExtras().getSerializable("Checkpoint");
                checkpoint = DatabaseHelper.getInstance(this).getCheckpoint(checkpoint.id);
                addCheckPoint(checkpoint);
                checkpointsAdded.add(checkpoint);
            }
        }
    }

    private void addCheckPoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
        switch (checkpoint.trigger){
            case Temperature:
                createCheckPoint(checkpoint.name, "Temp: "+checkpoint.temperature, checkpoint.id);
                break;
            case Time:
                createCheckPoint(checkpoint.name, "Time: "+checkpoint.minutes+":"+checkpoint.seconds, checkpoint.id);
                break;
            case TurnAround:
                createCheckPoint(checkpoint.name, "Turnaround", checkpoint.id);
                break;
            case PromptAtTemp:
                createCheckPoint(checkpoint.name, "Prompt at "+checkpoint.temperature, checkpoint.id);
                break;
            default:
                createCheckPoint(checkpoint.name, "", checkpoint.id);
                break;
        }
    }

    public Context getContext(){
        return this;
    }

    @Override
    public void onDataLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupViewMode(false, client.roast);
                Spinner spinner = findViewById(R.id.roastparamactivity_bean_spinner);
                //set spinner drop down elements
                List<String> categories = new ArrayList<String>();
                categories.add(client.bean.name);
                setupSpinner(spinner, categories);
                Button downloadBut = findViewById(R.id.roastparamactivity_add_button);
                downloadBut.setText("Download");
                downloadBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
                        int beanid = db.addBean(client.roast.bean);
                        client.roast.bean.id = beanid;
                        for(Checkpoint check: client.roast.checkpoints){
                            int checkId = db.addCheckpoint(check);
                            check.id = checkId;
                        }
                        db.addRoast(client.roast);

                        finish();
                    }
                });
            }
        });
    }
}