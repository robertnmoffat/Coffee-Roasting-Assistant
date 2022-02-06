package com.example.roastingassistant.user_interface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.roastingassistant.user_interface.menu.MenuOnClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.RoastRecord;
import Utilities.CommonFunctions;
import Utilities.DataCleaner;
import Utilities.DataSaver;
import Utilities.GlobalSettings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roastingassistant.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for viewing an individual roast's data which is saved to disk
 */
public class RoastDataViewActivity extends AppCompatActivity {
    RoastRecord record;
    ArrayList<Integer> temps;
    ArrayList<Integer> checkpoints;

    XYPlot plot;//UI graph plot

    int OVERLAY_REQUEST = 1;//request tag for callback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_data_view);

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new MenuOnClickListener(this));

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

        EditText startWeight = findViewById(R.id.roastdataview_startweight_edittext);
        startWeight.setText(CommonFunctions.formatWeightStringNumber(record.startWeightPounds, getContext()));
        boolean isMetric = GlobalSettings.getSettings(this).isMetric();
        TextView startWeightSuffix = findViewById(R.id.roastdataview_startweight_textview);
        startWeightSuffix.setText(isMetric?"Kgs":"Lbs");
        EditText endWeight = findViewById(R.id.roastdataview_endweight_edittext);
        endWeight.setText(CommonFunctions.formatWeightStringNumber(record.endWeightPounds, getContext()));
        TextView endWeightSuffix = findViewById(R.id.roastdataview_endweight_textview);
        endWeightSuffix.setText(isMetric?"Kgs":"Lbs");
        TextView percentView = findViewById(R.id.roastdataview_losspercent_textview);
        percentView.setText(String.format("%.2f", (1-record.endWeightPounds/record.startWeightPounds))+"%");

        Button updateButton = findViewById(R.id.roastdataview_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float startWeightFloat = Float.parseFloat(startWeight.getText().toString());
                float endWeightFloat = Float.parseFloat(endWeight.getText().toString());

                if(isMetric){//if metric convert back to pounds
                    startWeightFloat = CommonFunctions.KgToPounds(startWeightFloat);
                    endWeightFloat = CommonFunctions.KgToPounds(endWeightFloat);
                }

                record.startWeightPounds = startWeightFloat;
                record.endWeightPounds = endWeightFloat;

                DatabaseHelper.getInstance(getContext()).updateRoastRecordWeights(record);
                Toast.makeText(getContext(), "Record weights updated.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        DataSaver.loadRoastData(record, temps, checkpoints, this);
        setupGraph();
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        long total = end-start;

        LinearLayout layout = findViewById(R.id.roastdataview_layout);
        for(int i=0; i<record.roastProfile.checkpoints.size(); i++) {
            Checkpoint checkpoint = record.roastProfile.checkpoints.get(i);
            TextView checkDescription = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(CommonFunctions.dp(20, getResources()), CommonFunctions.dp(20, getResources()), 0, 0);
            checkDescription.setLayoutParams(params);

            if(i*2>=checkpoints.size())
                break;
            int mins = (int)(checkpoints.get(i*2)/60);
            float secs = checkpoints.get(i*2)-(mins*60);
            String secString = String.format("%.1f", secs);
            String timeString = ""+mins+":"+secString;
            String tempString = CommonFunctions.formatTempString(checkpoints.get(i*2+1), this);
            checkDescription.setText(checkpoint.name + " \n\t" + "Time: " + timeString+ " \n\t" + "Temp: " + tempString);

            checkDescription.setTextColor(getResources().getColor(R.color.white));
            checkDescription.setTextSize(CommonFunctions.dp(8, getResources()));
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

    /**
     * Get activity context for anonymous inner classes
     * @return
     */
    public Context getContext() {
        return this;
    }

    /**
     * Called after selecting a second roast to overlay on the temperature graph.
     * Loads that roast's data from DB and adds it to graph.
     * @param requestCode should be OVERLAY_REQUEST
     * @param resultCode should be RESULT_OK
     * @param data intent containing DB ID of roast to be overlaid
     */
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

    /**
     * Called if an invalid record ID is provided or it is not found in the DB.
     * Closes activity.
     */
    public void notFound(){
        Toast.makeText(this, "Roast record not found.", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Creates graph with the temp and checkpoint data from the selected roast.
     */
    public void setupGraph(){
        SimpleXYSeries series = new SimpleXYSeries("Temp");
        series.setModel(temps, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);

        SimpleXYSeries checkpointSeries = new SimpleXYSeries("Checkpoints");
        checkpointSeries.setModel(checkpoints, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);

        plot.addSeries(series, new LineAndPointFormatter(Color.YELLOW, null, null, null));
        LineAndPointFormatter checkpointFormatter =new LineAndPointFormatter(null, Color.GREEN, null, null);
        checkpointFormatter.getVertexPaint().setStrokeWidth(30);
        plot.addSeries(checkpointSeries, checkpointFormatter);
        plot.redraw();
    }

    /**
     * For adding a second roast's data to the graph.
     * @param temps temp ArrayList to be displayed
     * @param checkpoints checkpoint ArrayList to be displayed
     */
    public void setupGraphOverlay(ArrayList<Integer> temps, ArrayList<Integer> checkpoints){
        SimpleXYSeries series = new SimpleXYSeries("Temp");
        series.setModel(temps, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);

        SimpleXYSeries checkpointSeries = new SimpleXYSeries("Checkpoints");
        checkpointSeries.setModel(checkpoints, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);

        plot.addSeries(series, new LineAndPointFormatter(Color.BLUE, null, null, null));//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));
        LineAndPointFormatter checkpointFormatter =new LineAndPointFormatter(null, Color.RED, null, null);
        checkpointFormatter.getVertexPaint().setStrokeWidth(30);
        plot.addSeries(checkpointSeries, checkpointFormatter);
        plot.redraw();
    }
}