package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roastingassistant.R;

public class RoastActivity extends AppCompatActivity {
    boolean stopped=true;
    Button stopButton;
    Button undoButton;
    Button checkpointButton;

    TextView timeText;

    LinearLayout checkpointsLayout;

    float currentTime = 0.0f;
    float lastTime;
    String timeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast);

        stopButton = findViewById(R.id.roastactivity_stop_button);
        undoButton = findViewById(R.id.roastactivity_undocheckpoint_button);
        checkpointButton = findViewById(R.id.roastactivity_checkpoint_button);
        timeText = findViewById(R.id.roastactivity_time_textview);
        checkpointsLayout = findViewById(R.id.roastactivity_checkpoint_linearlayout);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stopped){
                    startStop();
                }
            }
        });

        checkpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stopped){
                    startStop();
                }
            }
        });

        Handler h = new Handler();
        int delay = 100; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                if(!stopped) {
                    currentTime += System.nanoTime()-lastTime;
                    timeString = String.format("%.1f", currentTime/1000000000);
                    timeText.setText("Time:" + timeString);
                    lastTime = System.nanoTime();
                }
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    public void startStop(){
        if(stopped){
            checkpointButton.setText("Checkpoint");
            stopped = false;
            lastTime = System.nanoTime();
        }else{
            checkpointButton.setText("Resume");
            stopped = true;
        }
    }
}