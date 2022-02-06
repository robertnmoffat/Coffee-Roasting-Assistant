package com.example.roastingassistant.user_interface;

import Database.DbData;
import Utilities.CommonFunctions;
import Utilities.GlobalSettings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
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

import Database.Bean;
import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;
import Networking.HttpClient;

/**
 * Activity for displaying and handling alterations on different roasts roasting parameters.
 */
public class RoastParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, HttpCallback {
    Roast roastToRoast;

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

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

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

    /**
     * Copy UI information into a new Roast object.
     * @return Roast object with UI information
     */
    public Roast getRoast(){
        Roast roast = new Roast();
        roast.name = ((EditText)findViewById(R.id.roastparamactivity_name_edittext)).getText().toString();
        roast.bean = beans.get(beanSpinnerSelection-1);
        roast.roastLevel = ((EditText)findViewById(R.id.roastparamactivity_roastlevel_edittext)).getText().toString();
        String chargeText = ((EditText)findViewById(R.id.roastparamactivity_chargetemp_edittext)).getText().toString();
        int charge = 0;
        try {
            charge = Integer.parseInt(chargeText);
        }catch(java.lang.NumberFormatException e){
            charge = 0;
        }
        roast.chargeTemp = charge;
        String dropText = ((EditText)findViewById(R.id.roastparamactivity_droptemp_edittext)).getText().toString();
        int temp=0;
        try {
            temp = Integer.parseInt(dropText);
        }catch(java.lang.NumberFormatException e){
            temp = 0;
        }
        roast.dropTemp = temp;
        roast.flavour = ((EditText)findViewById(R.id.roastparamactivity_flavour_edittext)).getText().toString();
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
     * @param name the name of the checkpoint
     * @param description description of the checkpoint
     */
    public void createCheckPoint(String name, String description, int arrayPos){
        LinearLayout checkLayout = findViewById(R.id.roastparamactivity_checkpoints_layout);
        TextView checkDescription = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,CommonFunctions.dp(10,getResources()), CommonFunctions.dp(40,getResources()), CommonFunctions.dp(10,getResources()));
        params.gravity = Gravity.RIGHT;
        checkDescription.setLayoutParams(params);
        checkDescription.setText(name+" "+description);
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

        if(curMode==mode.adding) {
            Button removeButton = new Button(this);
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params3.setMargins(CommonFunctions.dp(20,this.getResources()),0,0,0);
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
                    ((ViewGroup)textAndButton).removeView(checkDescription);
                    ((ViewGroup)textAndButton).removeView(removeButton);
                    checkpointsAdded.remove(arrayPos);
                }
            });

            textAndButton.addView(removeButton);
        }

        textAndButton.addView(checkDescription);
        checkLayout.addView(textAndButton);
    }

    /**
     * Setup click listener which starts an activity to enter checkpoint information.
     */
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
                    checkpoint = DatabaseHelper.getInstance(getContext()).getCheckpoint(checkpoint.id);
                    addCheckPoint(checkpoint);
                }

            }
        });
    }

    /**
     * Setup spinner containing previously used checkpoints in the database.
     * so that user doesn't need to fill out already created checkpoints a second time.
     */
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
        int temp = roast.dropTemp;
        char c = GlobalSettings.getSettings(this).isMetric()?'C':'F';
        if(GlobalSettings.getSettings(this).isMetric()){
            temp = CommonFunctions.standardTempToMetric(temp);
        }
        String dropTempStr = ""+temp+c;
        dropEd.setText(dropTempStr);
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
            int fortydp = CommonFunctions.dp(40, getResources());
            int tendp = CommonFunctions.dp(10, getResources());

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
                    roastToRoast = roast;

                    if(GlobalSettings.getSettings(getContext()).isCalibrated()){
                        Intent roastIntent = new Intent(context, RoastActivity.class);
                        roastIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        roastIntent.putExtra("RoastId", roast.id);
                        //intent.putExtras(extra);
                        //startActivityForResult(intent, 0);
                        finish();
                        startActivity(roastIntent);
                        overridePendingTransition(0,0); //0 for no animation
                    }else{
                        requestCalibration();
                    }
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
     * @param spinner spinner to add the item names to
     * @param itemNames the item names
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

        //----Bean Spinner----
        if(name.equals("Bean")){
            beanSpinnerSelection = (int)id;

            switch((int)id){
                case 0:
                    Log.i("Spinner", "Bean selected.");
                    break;
            }
        //-----Checkpoint Spinner----
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

    /**
     * Add passed Checkpoint object to array and display it on UI
     * @param checkpoint checkpoint object to be added and displayed
     */
    private void addCheckPoint(Checkpoint checkpoint) {
        String tempString = CommonFunctions.formatTempString(checkpoint.temperature, this);

        checkpoints.add(checkpoint);
        checkpointsAdded.add(checkpoint);
        int pos = checkpointsAdded.size()-1;
        switch (checkpoint.trigger){
            case Temperature:
                createCheckPoint(checkpoint.name, "Temp: "+tempString, pos);
                break;
            case Time:
                createCheckPoint(checkpoint.name, "Time: "+checkpoint.minutes+":"+checkpoint.seconds, pos);
                break;
            case TurnAround:
                createCheckPoint(checkpoint.name, "Turnaround", pos);
                break;
            case PromptAtTemp:
                createCheckPoint(checkpoint.name, "Prompt at "+tempString, pos);
                break;
            default:
                createCheckPoint(checkpoint.name, "", pos);
                break;
        }
    }

    /**
     * Get activity context for anonymous inner classes.
     * @return
     */
    public Context getContext(){
        return this;
    }

    /**
     * Called when viewing a roast on the server and it has been loaded.
     */
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

    /**
     * Prompt user if they would like to calibrate the recognition before roasting.
     */
    public void requestCalibration(){
        Roast roast = roastToRoast;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        GlobalSettings.getSettings(getContext()).setCalibrated(true,getContext());
                        Intent intent = new Intent(getContext(), CameraCalibrationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0); //0 for no animation
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Intent roastIntent = new Intent(context, RoastActivity.class);
                        roastIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        roastIntent.putExtra("RoastId", roast.id);
                        finish();
                        startActivity(roastIntent);
                        overridePendingTransition(0,0); //0 for no animation
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Calibrate temperature recognition?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}