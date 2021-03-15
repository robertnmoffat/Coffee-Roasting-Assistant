package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.CheckpointParamActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying and handling alterations on different roasts roasting parameters.
 */
public class RoastParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    enum mode{
        adding,
        viewing,
        downloading
    }

    int checkpointId = 0;
    mode curMode = mode.adding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_param);

        context = this.context;

        getMode(savedInstanceState);//set the mode of the activity

        switch (curMode){
            case adding:

                break;
            case viewing:
                setupViewMode();
                break;

            case downloading:
                setupDownloadMode();
                break;
        }

        setupAndLoadSpinners();

        setupCheckpoints();

        //---------Handle clicks on the button for adding the roast parameters to a new roast entry in the database.
        Button roastAddButton = findViewById(R.id.roastactivity_add_button);
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

    public void setupCheckpoints(){
        //-------Handle clicks on the button for adding new roast checkpoints to the current roast.
        Button addCheckpointButton = findViewById(R.id.roastactivity_add_checkpoint_button);
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

    public void setupAndLoadSpinners(){
        Spinner spinner = findViewById(R.id.roastActivity_bean_spinner);
        //set spinner drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Beans");//TODO: add stuff from database later
        categories.add("second");
        categories.add("third");
        setupSpinner(spinner, categories);

        Spinner checkPointSpinner = findViewById(R.id.roastactivity_checkpoint_spinner);
        //set spinner drop down elements
        categories = new ArrayList<String>();
        categories.add("New "+getString(R.string.checkpoint_text));//TODO: add stuff from database later
        setupSpinner(checkPointSpinner, categories);
    }

    /**
     * Sets up the activity for viewing already existing roasts
     */
    public void setupViewMode(){
        EditText ed = findViewById(R.id.roastactivity_name_edittext);
        ed.setText("Brazil Dark");
        ed.setEnabled(false);
    }

    /**
     * Sets up the activity for viewing downloadable roasts from the server
     */
    public void setupDownloadMode(){
        setupViewMode();

        Button downloadButton = findViewById(R.id.roastactivity_add_button);
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
        String name = parent.getId()==R.id.roastActivity_bean_spinner? "Bean":"Checkpoint";
        Log.i("Spinner", "ID: "+id+" View: "+name);

        if(parent.getId()==R.id.roastActivity_bean_spinner){
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