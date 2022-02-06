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
import com.example.roastingassistant.user_interface.menu.MenuOnClickListener;

import Database.Bean;
import Database.Blend;
import Database.DatabaseHelper;
import Networking.HttpClient;

/**
 * Activity for displaying and editing different un-roasted green bean information.
 */
public class BeanActivity extends AppCompatActivity implements HttpCallback{
    boolean viewing = false;
    HttpClient client;

    /**
     * Sets up the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean);

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

        Intent intent = getIntent();
        int id = intent.getIntExtra("Id",-1);
        if(id!=-1&&id!=0){//If there is an id value in the intent then it is to be viewed.
            viewing = true;
            Bean bean = db.getBean(id);//get the bean from the database using the id in the intent.
            setupForViewing(bean);
        }
        int serverId = intent.getIntExtra("serverId",-1);
        if(serverId!=-1&&serverId!=0){//If there is a serverId value in the intent it is to be downloaded from the server
            client = new HttpClient();
            client.idToGet = serverId;
            client.functionToPerform = HttpClient.HttpFunction.getBean;
            client.setLoadedCallback(this);
            client.execute();//execute the passed function request and use this as a callback to return the bean value.
        }

        Button roastAddButton = findViewById(R.id.beanactivity_add_button);
        if(viewing)
            roastAddButton.setText("Done");
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewing) {
                    //Transfer all bean information to bean object to be added to database
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

    /**
     * Transfers a Bean objects information to the UI elements
     * @param bean bean to be viewed
     */
    private void setupForViewing(Bean bean) {
        EditText nameET = findViewById(R.id.beanactivity_name_edittext);
        nameET.setText(bean.name);
        nameET.setEnabled(false);
        EditText originET = findViewById(R.id.beanactivity_origin_edittext);
        originET.setText(bean.origin);
        originET.setEnabled(false);
        EditText farmET = findViewById(R.id.beanactivity_farm_edittext);
        farmET.setText(bean.farm);
        farmET.setEnabled(false);
        EditText dryingET = findViewById(R.id.beanactivity_drymethod_edittext);
        dryingET.setText(bean.dryingMethod);
        dryingET.setEnabled(false);
        EditText processET = findViewById(R.id.beanactivity_process_edittext);
        processET.setText(bean.process);
        processET.setEnabled(false);
        EditText flavoursET = findViewById(R.id.beanactivity_flavour_edittext);
        flavoursET.setText(bean.flavours);
        flavoursET.setEnabled(false);
    }

    /**
     * Get the context of this activity. (For inner anonymous classes to reference)
     * @return context
     */
    public Context getContext(){
        return this;
    }

    /**
     * Loads data once it has finished download from the server.
     */
    @Override
    public void onDataLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupForViewing(client.bean);
                Button downloadBut = findViewById(R.id.beanactivity_add_button);
                downloadBut.setText("Download");
            }
        });
    }
}