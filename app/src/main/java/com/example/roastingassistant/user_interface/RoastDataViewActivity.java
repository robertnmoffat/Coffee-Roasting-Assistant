package com.example.roastingassistant.user_interface;

import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import Database.DatabaseHelper;
import Database.RoastRecord;
import Utilities.DataSaver;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

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
        if(id==-1)notFound();

        record = DatabaseHelper.getInstance(this).getRoastRecord(id);
        if(record==null)notFound();

        temps = new ArrayList<>();
        checkpoints = new ArrayList<>();

        DataSaver.loadRoastData(record, temps, checkpoints, this);
        setupGraph();
    }

    public void notFound(){
        Toast.makeText(this, "Roast record not found.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void setupGraph(){
        XYPlot plot = findViewById(R.id.plot);
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
}