package com.example.roastingassistant.user_interface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.RoastRecord;
import Utilities.DataSaver;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roastingassistant.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RoastDataViewActivity extends AppCompatActivity {
    RoastRecord record;
    ArrayList<Integer> temps;
    ArrayList<Integer> checkpoints;

    XYPlot plot;

    int OVERLAY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_data_view);

        TextView title = findViewById(R.id.roastdataview_title_textview);
        Button doneButton = findViewById(R.id.roastdataview_done_button);
        plot = findViewById(R.id.plot);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int overlayId = getIntent().getIntExtra("overlayId", -1);//TODO

        int id = getIntent().getIntExtra("recordId", -1);
        if(id==-1)notFound();

        record = DatabaseHelper.getInstance(this).getRoastRecord(id);
        if(record==null)notFound();

        title.setText(record.name);

        temps = new ArrayList<>();
        checkpoints = new ArrayList<>();

        DataSaver.loadRoastData(record, temps, checkpoints, this);
        setupGraph();

        LinearLayout layout = findViewById(R.id.roastdataview_layout);
        for(int i=0; i<record.roastProfile.checkpoints.size(); i++) {
            Checkpoint checkpoint = record.roastProfile.checkpoints.get(i);
            TextView checkDescription = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(Utils.dp(20, getResources()), Utils.dp(20, getResources()), 0, 0);
            checkDescription.setLayoutParams(params);

            if(i*2>=checkpoints.size())
                break;
            int mins = (int)(checkpoints.get(i*2)/60);
            float secs = checkpoints.get(i*2)-(mins*60);
            String secString = String.format("%.1f", secs);
            String timeString = ""+mins+":"+secString;
            checkDescription.setText(checkpoint.name + " \n\t" + timeString+ " \n\t" + checkpoints.get(i*2+1) + " degrees");

            checkDescription.setTextColor(getResources().getColor(R.color.white));
            //checkDescription.setBackgroundColor(getResources().getColor(R.color.grayBack));
            checkDescription.setTextSize(Utils.dp(8, getResources()));
            //checkDescription.setGravity(Gravity.RIGHT);
            layout.addView(checkDescription);
        }

        Button overlayButton = new Button(this);
        overlayButton.setText("Add overlay");
        overlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PreviousRoastActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("returnResult", true);
                startActivityForResult(intent, OVERLAY_REQUEST);
                overridePendingTransition(0,0); //0 for no animation
            }
        });
        layout.addView(overlayButton);
    }

    public Context getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==OVERLAY_REQUEST){
            if(resultCode==RESULT_OK) {
                Log.i("callback", "Callback received!");
                int overlayId = data.getIntExtra("overlayId", -1);
                RoastRecord record = DatabaseHelper.getInstance(this).getRoastRecord(overlayId);

                ArrayList<Integer> temps = new ArrayList<>();
                ArrayList<Integer> checkpoints = new ArrayList<>();

                DataSaver.loadRoastData(record, temps, checkpoints, this);
                setupGraphOverlay(temps, checkpoints);
            }
        }
    }

    public void notFound(){
        Toast.makeText(this, "Roast record not found.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void setupGraph(){
        SimpleXYSeries series = new SimpleXYSeries("Temp");
        series.setModel(temps, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
        //plot.addSeries(series, new LineAndPointFormatter());//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));

        SimpleXYSeries checkpointSeries = new SimpleXYSeries("Checkpoints");
        checkpointSeries.setModel(checkpoints, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
        //plot.addSeries(checkpoints, new LineAndPointFormatter());

        plot.addSeries(series, new LineAndPointFormatter(Color.YELLOW, null, null, null));//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));
        LineAndPointFormatter checkpointFormatter =new LineAndPointFormatter(null, Color.GREEN, null, null);
        checkpointFormatter.getVertexPaint().setStrokeWidth(30);
        plot.addSeries(checkpointSeries, checkpointFormatter);
        plot.redraw();
    }

    public void setupGraphOverlay(ArrayList<Integer> temps, ArrayList<Integer> checkpoints){
        SimpleXYSeries series = new SimpleXYSeries("Temp");
        series.setModel(temps, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
        //plot.addSeries(series, new LineAndPointFormatter());//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));

        SimpleXYSeries checkpointSeries = new SimpleXYSeries("Checkpoints");
        checkpointSeries.setModel(checkpoints, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
        //plot.addSeries(checkpoints, new LineAndPointFormatter());

        plot.addSeries(series, new LineAndPointFormatter(Color.BLUE, null, null, null));//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));
        LineAndPointFormatter checkpointFormatter =new LineAndPointFormatter(null, Color.RED, null, null);
        checkpointFormatter.getVertexPaint().setStrokeWidth(30);
        plot.addSeries(checkpointSeries, checkpointFormatter);
        plot.redraw();
    }
}