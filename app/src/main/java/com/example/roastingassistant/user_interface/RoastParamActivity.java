package com.example.roastingassistant.user_interface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.roastingassistant.R;

import java.util.ArrayList;
import java.util.List;

import Database.Checkpoint;
import Database.Roast;

/**
 * Activity for displaying and handling alterations on different roasts roasting parameters.
 */
public class RoastParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public enum mode{
        adding,
        viewing,
        downloading
    }

    public static final int CHECKPOINT_REQUEST = 1;

    int checkpointId = 0;
    mode curMode = mode.adding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_param);

        context = this;

        getMode(savedInstanceState);//set the mode of the activity

        switch (curMode){
            case adding:
                //Default layout
                break;
            case viewing:
                Roast roast = new Roast();
                roast.name = "Brazil Dark";
                roast.roastLevel = "Dark";
                roast.dropTemp = 459;
                Checkpoint check = new Checkpoint();
                check.name = "Heat to 3.5";
                check.temperature = 300;
                roast.checkpoints.add(check);
                setupViewMode(true, roast);
                break;

            case downloading:
                setupDownloadMode();
                break;
        }

        setupAndLoadSpinners();

        setupCheckpoints();

        //---------Handle clicks on the button for adding the roast parameters to a new roast entry in the database.
        Button roastAddButton = findViewById(R.id.roastparamactivity_add_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


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
    public void createCheckPoint(String name, String description){
        LinearLayout checkLayout = findViewById(R.id.roastparamactivity_checkpoints_layout);
        TextView checkDescription = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,Utils.dp(10,getResources()), Utils.dp(10,getResources()), Utils.dp(10,getResources()));
        checkDescription.setLayoutParams(params);
        checkDescription.setText(name+" "+description);
        checkDescription.setTextColor(getResources().getColor(R.color.lightGray));
        checkDescription.setBackgroundColor(getResources().getColor(R.color.grayBack));
        checkDescription.setTextSize(Utils.dp(7, getResources()));
        checkDescription.setGravity(Gravity.CENTER);

        LinearLayout textAndButton = new LinearLayout(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(0,Utils.dp(20,getResources()), 0, 0);
        params2.gravity = Gravity.CENTER;
        textAndButton.setLayoutParams(params2);
        textAndButton.setOrientation(LinearLayout.HORIZONTAL);
        int id = ViewCompat.generateViewId();//TODO:Store id in array
        textAndButton.setId(id);//set to generated id
        textAndButton.addView(checkDescription);
        textAndButton.setGravity(Gravity.CENTER);

        if(curMode==mode.adding) {
            Button removeButton = new Button(this);
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params3.gravity = Gravity.CENTER;
            removeButton.setLayoutParams(params3);
            removeButton.setMaxWidth(Utils.dp(10, getResources()));
            removeButton.setMaxHeight(Utils.dp(10, getResources()));
            removeButton.setText("-");
            removeButton.setTextColor(getResources().getColor(R.color.white));
            removeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
            removeButton.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));
            removeButton.setGravity(Gravity.CENTER);

            textAndButton.addView(removeButton);
        }

        checkLayout.addView(textAndButton);
    }

    public void setupCheckpoints(){
        //-------Handle clicks on the button for adding new roast checkpoints to the current roast.
        Button addCheckpointButton = findViewById(R.id.roastparamactivity_add_checkpoint_button);
        addCheckpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(checkpointId){
                    case 0:
                        Intent intent = new Intent(context, CheckpointParamActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, CHECKPOINT_REQUEST);
                        overridePendingTransition(0,0); //0 for no animation

                        break;

                }

            }
        });
    }

    public void setupAndLoadSpinners(){
        Spinner spinner = findViewById(R.id.roastparamactivity_bean_spinner);
        //set spinner drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Beans");//TODO: add stuff from database later
        categories.add("second");
        categories.add("third");
        setupSpinner(spinner, categories);

        Spinner checkPointSpinner = findViewById(R.id.roastparamactivity_checkpoint_spinner);
        //set spinner drop down elements
        categories = new ArrayList<String>();
        categories.add("New "+getString(R.string.checkpoint_text));//TODO: add stuff from database later
        setupSpinner(checkPointSpinner, categories);
    }

    /**
     * Sets up the activity for viewing already existing roasts
     * @param startButton Whether or not to display the button to start the roast.
     */
    public void setupViewMode(boolean startButton, Roast roast){
        EditText nameEd = findViewById(R.id.roastparamactivity_name_edittext);
        nameEd.setText(roast.name);
        nameEd.setEnabled(false);

        Spinner beanSpinner = findViewById(R.id.roastparamactivity_bean_spinner);
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
                    //intent.putExtras(extra);
                    //startActivityForResult(intent, 0);
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
        setupViewMode(false, new Roast());

        Button downloadButton = findViewById(R.id.roastparamactivity_add_button);
        downloadButton.setText("Download");
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

        if(parent.getId()==R.id.roastparamactivity_bean_spinner){
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
                Log.i("poop", "Callback received!");
                Checkpoint checkpoint = (Checkpoint) (Checkpoint)data.getExtras().getSerializable("Checkpoint");
                addCheckPoint(checkpoint);
            }
        }
    }

    private void addCheckPoint(Checkpoint checkpoint) {
        switch (checkpoint.trigger){
            case Temperature:
                createCheckPoint(checkpoint.name, "Temp: "+checkpoint.temperature);
                break;
            case Time:
                createCheckPoint(checkpoint.name, "Time: "+checkpoint.minutes+":"+checkpoint.seconds);
                break;
            case TurnAround:
                createCheckPoint(checkpoint.name, "Turnaround");
                break;
            case PromptAtTemp:
                createCheckPoint(checkpoint.name, "Promp at "+checkpoint.temperature);
                break;
            default:
                createCheckPoint(checkpoint.name, "");
                break;
        }
    }
}