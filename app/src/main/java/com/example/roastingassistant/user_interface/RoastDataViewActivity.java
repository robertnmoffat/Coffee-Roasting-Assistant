package com.example.roastingassistant.user_interface;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import Database.DatabaseHelper;
import Database.RoastRecord;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.roastingassistant.R;

import java.util.ArrayList;

public class RoastDataViewActivity extends AppCompatActivity {
    RoastRecord record;
    ArrayList<Integer> temps;
    ArrayList<Integer> checkpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_data_view);

        int id = getIntent().getIntExtra("recordId", -1);
        if(id==-1)finish();

        record = DatabaseHelper.getInstance(this).getRoastRecord(id);
        if(record==null)finish();

    }
}