package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.roastingassistant.R;

import Database.Bean;
import Database.Blend;
import Database.DatabaseHelper;

/**
 * Activity for displaying and handling alterations on different un-roasted green bean's information.
 */
public class BeanActivity extends AppCompatActivity {
    boolean viewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean);

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        Intent intent = getIntent();
        int id = intent.getIntExtra("Id",-1);
        if(id!=-1&&id!=0){
            viewing = true;
            Bean bean = db.getBean(id);
            setupForViewing(bean);
        }

        Button roastAddButton = findViewById(R.id.beanactivity_add_button);
        if(viewing)
            roastAddButton.setText("Done");
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewing) {
                    Bean bean = new Bean();
                    bean.name = ((EditText) findViewById(R.id.beanactivity_name_edittext)).getText().toString();
                    bean.origin = ((EditText) findViewById(R.id.beanactivity_origin_edittext)).getText().toString();
                    bean.farm = ((EditText) findViewById(R.id.beanactivity_farm_edittext)).getText().toString();
                    bean.dryingMethod = ((EditText) findViewById(R.id.beanactivity_drymethod_edittext)).getText().toString();
                    bean.process = ((EditText) findViewById(R.id.beanactivity_process_edittext)).getText().toString();
                    bean.flavours = ((EditText) findViewById(R.id.beanactivity_flavour_edittext)).getText().toString();

                    DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
                    db.addBean(bean);
                }

                finish();
            }
        });
    }

    private void setupForViewing(Bean bean) {
        EditText nameET = findViewById(R.id.beanactivity_name_edittext);
        nameET.setText(bean.name);
        EditText originET = findViewById(R.id.beanactivity_origin_edittext);
        originET.setText(bean.origin);
        EditText farmET = findViewById(R.id.beanactivity_farm_edittext);
        farmET.setText(bean.farm);
        EditText dryingET = findViewById(R.id.beanactivity_drymethod_edittext);
        dryingET.setText(bean.dryingMethod);
        EditText processET = findViewById(R.id.beanactivity_process_edittext);
        processET.setText(bean.process);
        EditText flavoursET = findViewById(R.id.beanactivity_flavour_edittext);
        flavoursET.setText(bean.flavours);
    }

    public Context getContext(){
        return this;
    }
}