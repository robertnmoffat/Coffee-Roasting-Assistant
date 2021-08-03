package com.example.roastingassistant.user_interface;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.roastingassistant.R;

import androidx.appcompat.app.AppCompatActivity;

public class PreviousRoastActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_roast);

    }
}