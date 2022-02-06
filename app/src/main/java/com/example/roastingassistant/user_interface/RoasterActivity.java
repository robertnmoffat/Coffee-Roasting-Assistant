package com.example.roastingassistant.user_interface;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import Database.DatabaseHelper;
import Database.Roaster;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roastingassistant.R;

/**
 * Activity for saving information on the users roasting machine.
 */
public class RoasterActivity extends AppCompatActivity {
    boolean updateDb = false;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roaster);

        Roaster roaster = DatabaseHelper.getInstance(this).getRoaster(1);
        if(roaster!=null) {
            setViews(roaster);
            updateDb = true;
            id = roaster.id;
        }

        Button button = findViewById(R.id.roasteractivity_update_button);
        setupUpdateButton(button);
    }

    /**
     * Copy UI fields into Roaster object and pass it to DB to be added.
     * @param button Update button
     */
    private void setupUpdateButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Roaster roaster = new Roaster();
                roaster.id = id;
                roaster.name = ((EditText)findViewById(R.id.roasteractivity_name_edittext)).getText().toString();

                if(roaster.name.equals("")){
                    Toast.makeText(getContext(), "Name required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                roaster.description = ((EditText) findViewById(R.id.roasteractivity_details_edittext)).getText().toString();
                String drumSpeedString = ((EditText) findViewById(R.id.roasteractivity_drumspeed_edittext)).getText().toString();
                if (!drumSpeedString.equals(""))
                    roaster.drumSpeed = Float.parseFloat(drumSpeedString);
                roaster.heatingType = ((EditText) findViewById(R.id.roasteractivity_heating_edittext)).getText().toString();
                String capacityString = ((EditText) findViewById(R.id.roasteractivity_capacity_edittext)).getText().toString();
                if (!capacityString.equals(""))
                    roaster.capacityPounds = Float.parseFloat(capacityString);
                roaster.brand = ((EditText) findViewById(R.id.roasteractivity_brand_edittext)).getText().toString();

                if (updateDb)
                    DatabaseHelper.getInstance(getContext()).updateRoaster(roaster);
                else
                    roaster.id = DatabaseHelper.getInstance(getContext()).addRoaster(roaster);

                finish();
            }
        });
    }

    /**
     * Set UI elements with passed Roaster object's information.
     * @param roaster Roaster information
     */
    public void setViews(Roaster roaster){
        ((EditText)findViewById(R.id.roasteractivity_name_edittext)).setText(roaster.name);
        ((EditText)findViewById(R.id.roasteractivity_details_edittext)).setText(roaster.description);
        ((EditText)findViewById(R.id.roasteractivity_brand_edittext)).setText(roaster.brand);
        ((EditText)findViewById(R.id.roasteractivity_capacity_edittext)).setText(""+roaster.capacityPounds);
        ((EditText)findViewById(R.id.roasteractivity_heating_edittext)).setText(roaster.heatingType);
        ((EditText)findViewById(R.id.roasteractivity_drumspeed_edittext)).setText(""+roaster.drumSpeed);
    }

    /**
     * get activity context for anonymous inner classes.
     * @return
     */
    public Context getContext(){
        return this;
    }
}