package com.example.roastingassistant.user_interface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.menu.MenuOnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Database.DatabaseHelper;
import Database.DbData;
import Database.RoastRecord;
import Utilities.CoffeeSpreadsheet;
import androidx.appcompat.app.AppCompatActivity;

public class PreviousRoastActivity extends AppCompatActivity {

    private TextView mTextView;
    LinearLayout layout;

    boolean returnResult = false;

    ArrayList<RoastRecord> records;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_roast);

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

        activity = this;

        Intent intent = getIntent();
        returnResult = intent.getBooleanExtra("returnResult", false);

        Button doneButton = findViewById(R.id.previousroast_done_button);
        Button exportButton = findViewById(R.id.previousroast_export_button);

        layout = findViewById(R.id.previousroast_roasts_layout);
        records = DatabaseHelper.getInstance(this).getAllRoastRecords();

        if(records==null){
            Toast.makeText(getContext(), "No Saved Records.", Toast.LENGTH_SHORT).show();
            return;
        }

        for(RoastRecord record: records){
            addButton(record);
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<RoastRecord> fullRecords = new ArrayList<>();
                fullRecords = DatabaseHelper.getInstance(getContext()).getAllRoastRecords();
                CoffeeSpreadsheet sheet = new CoffeeSpreadsheet(activity, fullRecords);
                sheet.sendSpreadsheet(getContext());
            }
        });
    }

    public Context getContext(){
        return this;
    }

    public void addButton(RoastRecord record){
        Button button = new Button(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(returnResult) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("overlayId", record.id);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                    return;
                }

                Intent intent = new Intent(getContext(), RoastDataViewActivity.class);
                //intent.putExtra("Mode", RoastParamActivity.mode.downloading);
                intent.putExtra("recordId", record.id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //startActivityForResult(intent, 0);
                startActivity(intent);
                overridePendingTransition(0, 0); //0 for no animation
                //Log.d("Server", data.name + " ID:" + data.serverId);
            }
        });



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        //String typename = data.typeName.substring(0, 1).toUpperCase() + data.typeName.substring(1);
        button.setText(record.name);
        button.setTextColor(getResources().getColor(R.color.lightGray));
        button.setBackgroundColor(getResources().getColor(R.color.grayBack));
        button.setId(R.id.databrowser_openRoast_button);
        //button.setMinHeight(fortydp*2+fortydp/2);
        //button.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));

        layout.addView(button);
    }
}