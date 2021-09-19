package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.roastingassistant.R;

import java.util.ArrayList;
import java.util.List;

import Database.Checkpoint;
import Database.DatabaseHelper;

/**
 * Activity for setting up a checkpoint to add to the database.
 */
public class CheckpointParamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Checkpoint.trig currentTrigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoint_param);

        Spinner spinner = findViewById(R.id.checkpoint_trigger_spinner);
        List<String> categories = new ArrayList<String>();
        for(Checkpoint.trig trigger: Checkpoint.trig.values()){
            categories.add(trigger.toString());
        }

        //spinner click listener
        spinner.setOnItemSelectedListener(this);
        //adaptor for categories and spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, categories);
        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        Button button = findViewById(R.id.checkpoint_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkpoint checkObj = createCheckpoint();//create a checkpoint obj based on the current user fields.

                long checkId = DatabaseHelper.getInstance(getContext().getApplicationContext()).addCheckpoint(checkObj);//Add checkpoint to db for re-usability
                checkObj.id = (int)checkId;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("Checkpoint", checkObj);//load the checkpoint object into the intent to be returned to the RoastParamActivity
                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });
    }

    /**
     * Creates and returns a checkpoint based on the current fields.
     * @return
     */
    Checkpoint createCheckpoint(){
        Checkpoint check = new Checkpoint();
        check.name = ((EditText)findViewById(R.id.checkpoint_name_edittext)).getText().toString();

        check.trigger = currentTrigger;

        String tempStr = ((EditText)findViewById(R.id.checkpoint_temp_edittext)).getText().toString();
        int tempInt = tempStr.equals("")?-1:Integer.parseInt(tempStr);
        check.temperature = tempInt;

        String minStr = ((EditText)findViewById(R.id.checkpoint_timeminute_edittext)).getText().toString();
        int minInt = minStr.equals("")?-1:Integer.parseInt(minStr);
        check.minutes = minInt;

        String secStr = ((EditText)findViewById(R.id.checkpoint_timesecond_edittext)).getText().toString();
        int secInt = secStr.equals("")?-1:Integer.parseInt(secStr);
        check.seconds = secInt;

        return check;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentTrigger = Checkpoint.trig.values()[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public Context getContext(){
        return this;
    }
}