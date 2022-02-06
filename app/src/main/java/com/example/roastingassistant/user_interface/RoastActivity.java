package com.example.roastingassistant.user_interface;

import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;
import Database.RoastRecord;
import NeuralNetwork.ImageProcessing;
import NeuralNetwork.NetworkController;
import NeuralNetwork.NeuralThread;
import Utilities.DataCleaner;
import Utilities.DataSaver;
import Utilities.CommonFunctions;
import Utilities.GlobalSettings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.roastingassistant.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import Camera.CameraPreview;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Activity from which roasting takes place.
 */
public class RoastActivity extends AbstractCamera {
    boolean testing = false;
    int testPos=1;
    ArrayList<Integer> testTemps;

    boolean turnedAround = false;
    boolean checkpointWarned = false;

    boolean stopped=true;
    Button stopButton;
    Button undoButton;
    Button checkpointButton;
    TextView checkpointText;

    TextView timeText;

    float currentTime = 0.0f;
    float lastTime;
    String timeString;

    ArrayList<Integer> tempsOverTime;
    ArrayList<Integer> safeTempsOverTime;
    ArrayList<Integer> checkpointTemps;

    MediaPlayer player;

    Roast roast;
    int currentCheckpoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        brightness = GlobalSettings.getSettings(this).getCameraBrightness();

        player = MediaPlayer.create(this,
                R.raw.ring);

        curTemp = new AtomicInteger();

        if(testing){
            testTemps = new ArrayList<>();
            RoastRecord record = new RoastRecord();
            record.filename = "Ethiopia light Wed Nov 03 10:27:42 PDT 2021";
            record.filesizeBytes = 14624;
            DataSaver.loadRoastData(record, testTemps, new ArrayList<>(), this);
        }

        ActivityCompat.requestPermissions(RoastActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, STORAGE_PERMISSION_CODE);
        setContentView(R.layout.activity_roast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkPermission();

        tempsOverTime = new ArrayList<>();
        safeTempsOverTime = new ArrayList<>();
        checkpointTemps = new ArrayList<>();

        networkController = new NetworkController(this);

        stopButton = findViewById(R.id.roastactivity_stop_button);
        undoButton = findViewById(R.id.roastactivity_undocheckpoint_button);
        checkpointButton = findViewById(R.id.roastactivity_checkpoint_button);
        timeText = findViewById(R.id.roastactivity_time_textview);
        checkpointsLayout = findViewById(R.id.roastactivity_checkpoint_linearlayout);
        tempText = findViewById(R.id.roastactivity_temp_textview);

        cameraPreview = findViewById(R.id.roastactivity_camera_Layout);

        int id = getIntent().getIntExtra("RoastId", -1);

        if(id!=-1) {
            roast = DatabaseHelper.getInstance(this).getRoast(id);
            Log.d("roastid", roast.name);
            checkpointText = findViewById(R.id.roastactivity_checkpoint_textview);
            updateCheckpointText();
        }


        Button zoomInButton = findViewById(R.id.roastactivity_zoomin_button);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int zoom = zoomIn();

                Log.d("zoomButton", "zoom in pressed. "+zoom);
            }
        });
        Button zoomOutButton = findViewById(R.id.roastactivity_zoomout_button);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOut();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stopped){
                    startStop();
                    promptForSave();
                }
            }
        });

        checkpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stopped){
                    startStop();
                }else{
                    triggerCheckpoint();
                }
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCheckpoint>0){
                    checkpointTemps.remove(checkpointTemps.size()-1);
                    checkpointTemps.remove(checkpointTemps.size()-1);
                    currentCheckpoint-=1;
                }
            }
        });

        Handler h = new Handler();
        int delay = 500; //milliseconds


        XYPlot plot = findViewById(R.id.plot);
        SimpleXYSeries series = new SimpleXYSeries("Temp");
        series.setModel(safeTempsOverTime, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
        plot.addSeries(series, new LineAndPointFormatter());//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));

        SimpleXYSeries checkpoints = new SimpleXYSeries("Checkpoints");
        checkpoints.setModel(checkpointTemps, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
        plot.addSeries(checkpoints, new LineAndPointFormatter());


        h.postDelayed(new Runnable(){
            public void run(){
                if(guessTextUpdated){
                    tempText.setText("Temperature:\n"+guessText);
                    //guessInt = Integer.parseInt(guessText);
                    guessTextUpdated=false;
                }
                if(imageRightUpdated){
                    //graphView.setImageBitmap(imageLeft, imageMid, imageRight);
                    imageRightUpdated=false;
                }

                if(!stopped) {
                    currentTime += (System.nanoTime()/1000000000.0f)-lastTime;
                    int mins = (int)(currentTime/60);
                    float secs = currentTime-(mins*60);
                    String secString = String.format("%2.0f", secs);
                    timeString = ""+mins+":"+secString;
                    timeText.setText("Time:" + timeString);
                    lastTime = System.nanoTime()/1000000000.0f;

                    String num = guessText;

                    if (turnedAround) {
                        fixNine();
                    }
                    recordSafeTemp();

                    if (testing) {
                        if(testPos<testTemps.size()) {
                            curTemp.set(testTemps.get(testPos));
                            guessText=""+curTemp.get();
                            guessTextUpdated=true;
                            tempsOverTime.add(testTemps.get(testPos-1));
                            tempsOverTime.add(testTemps.get(testPos));
                            testPos+=2;
                        }
                    } else {
                        tempsOverTime.add((int) currentTime);
                        tempsOverTime.add(curTemp.get());
                    }

                    if(lastSafeTemp()<190){
                        turnedAround=true;
                    }
                    //----Time Trigger-----
                    if (currentCheckpoint<roast.checkpoints.size() && roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.Time) {
                        if (!checkpointWarned && currentTime >= roast.checkpoints.get(currentCheckpoint).timeTotalInSeconds() - 7) {
                            player.start();
                            checkpointWarned = true;
                        }
                        if (currentTime >= roast.checkpoints.get(currentCheckpoint).timeTotalInSeconds()) {
                            triggerCheckpoint();
                        }
                    }
                    //----Other triggers after temperature turn around-------
                    if(currentCheckpoint<roast.checkpoints.size()&&turnedAround) {
                        if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.Temperature) {
                            if (!checkpointWarned && lastSafeTemp() >= roast.checkpoints.get(currentCheckpoint).temperature - 7) {
                                player.start();
                                checkpointWarned = true;
                            }
                            if (lastSafeTemp() >= roast.checkpoints.get(currentCheckpoint).temperature) {
                                triggerCheckpoint();
                            }
                        }else if(roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.PromptAtTemp){
                            if (!checkpointWarned && lastSafeTemp() >= roast.checkpoints.get(currentCheckpoint).temperature - 7) {
                                player.start();
                                checkpointWarned = true;
                            }
                        }
                    }

                    plot.removeSeries(series);
                    plot.removeSeries(checkpoints);
                    series.setModel(safeTempsOverTime, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
                    checkpoints.setModel(checkpointTemps, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);

                    plot.addSeries(series, new LineAndPointFormatter(Color.YELLOW, null, null, null));//new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));
                    LineAndPointFormatter checkpointFormatter = new LineAndPointFormatter(null, Color.GREEN, null, null);
                    checkpointFormatter.getVertexPaint().setStrokeWidth(30);
                    plot.addSeries(checkpoints, checkpointFormatter);
                    plot.redraw();
                }
                h.postDelayed(this, delay);
            }
        }, delay);

        //Start thread to run the image detection neural network.
        if(!testing) {
            NeuralThread thread = new NeuralThread(this);
            thread.start();
        }

        checkPermission();

    }

    /**
     * Gets the last temperature value in the array of temperature values.
     * @return last temperature value
     */
    public int lastSafeTemp(){
        if(safeTempsOverTime.size()>0)
            return safeTempsOverTime.get(safeTempsOverTime.size()-1);
        return curTemp.get();
    }

    /**
     * Trigger the next roasting checkpoint
     */
    public void triggerCheckpoint(){
        checkpointWarned = false;//reset checkpoint warning sound effect
        if(currentCheckpoint<roast.checkpoints.size()) {//if there are still checkpoints to trigger
            checkpointTemps.add((int) currentTime);
            if(safeTempsOverTime.size()!=0)//If there is a temperature to save
                checkpointTemps.add(safeTempsOverTime.get(safeTempsOverTime.size() - 1));
            else
                checkpointTemps.add(0);//if no temp to save, just add zero
        }
        currentCheckpoint++;
        if(currentCheckpoint<roast.checkpoints.size()) {
            updateCheckpointText();
        }else{
            checkpointText.setText("Roast complete.");//if all checkpoints have been triggered, the roast is complete.
        }
    }

    /**
     * Ensures that only filtered temps are added to the safeTempsOverTime array.
     */
    public void recordSafeTemp(){
        safeTempsOverTime = DataCleaner.medianFilter(tempsOverTime, 100);
    }

    /**
     * Updates the checkpoint text based on the type of the next checkpoint.
     */
    public void updateCheckpointText(){
        boolean isMetric = GlobalSettings.getSettings(this).isMetric();
        char c = isMetric?'C':'F';


        if(roast!=null) {
            if (roast.checkpoints.size() > 0&&currentCheckpoint<roast.checkpoints.size()) {
                int temp = isMetric?CommonFunctions.standardTempToMetric(roast.checkpoints.get(currentCheckpoint).temperature):roast.checkpoints.get(currentCheckpoint).temperature;

                if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.Temperature)
                    checkpointText.setText("Next checkpoint:\n"+roast.checkpoints.get(currentCheckpoint).name + " at " + temp+c );
                if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.Time)
                    checkpointText.setText("Next checkpoint:\n"+roast.checkpoints.get(currentCheckpoint).name + " at " + CommonFunctions.secondsToTimeString(roast.checkpoints.get(currentCheckpoint).timeTotalInSeconds()));
                if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.PromptAtTemp)
                    checkpointText.setText("Next checkpoint:\n"+roast.checkpoints.get(currentCheckpoint).name + " at " + temp+c);
            }
        }
    }

    /**
     * Release camera
     */
    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();
    }

    /**
     * Release camera hardware at end of use for other apps.
     */
    private void releaseCamera(){
        if(mCamera!=null){
            mCamera.stopPreview();;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Get activity context for anonymous inner class
     * @return this context
     */
    public Context getContext(){
        return this;
    }

    /**
     * Checks camera permission, and sets up preview if granted.
     */
    private void checkPermission(){
        PermissionListener permissionListener = new PermissionListener(){

            @Override
            public void onPermissionGranted() {
                setupPreview();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        //check camera permission
        TedPermission.with(this).setPermissionListener(permissionListener).setDeniedMessage("You will not be able to use the auto record feature without camera permission at [Setting] > [Permission]").setPermissions(Manifest.permission.CAMERA).check();
    }

    /**
     * Get bitmap of camera preview
     * @return returns new bitmap, or null if a new one has not yet been generated
     */
    public Bitmap getCameraBitmap(){
        if(cameraImageLoaded) {
            cameraImageLoaded=false;
            return cameraBitmap;
        }
        return null;
    }

    /**
     * Zoom in the camera
     * @return the current zoom level
     */
    private int zoomIn(){
        Camera.Parameters params = mCamera.getParameters();
        int zoom = params.getZoom();
        if(zoom+10>=params.getMaxZoom())return zoom;

        zoom+=10;
        params.setZoom(zoom);
        mCamera.setParameters(params);

        return zoom;
    }

    /**
     * Zoom out the camera
     * @return the current zoom level
     */
    private int zoomOut(){
        Camera.Parameters params = mCamera.getParameters();
        int zoom = params.getZoom();
        if(zoom-10<=0)return zoom;

        zoom-=10;
        params.setZoom(zoom);
        mCamera.setParameters(params);

        return zoom;
    }

    private void setupPreview(){
        mCamera = Camera.open();
        mPreview = new CameraPreview(getBaseContext(), mCamera);


        try{
            //try to set camera focus
            Camera.Parameters params = mCamera.getParameters();
            params.setZoom(params.getMaxZoom()/2);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
        }catch(Exception e){

        }


        cameraPreview.removeAllViews();


        cameraPreview.addView(mPreview);
        //mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        mPreview.refreshCamera(mCamera);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onPreviewFrame(byte[] bytes, Camera camera) {
                            if(camera==null||cameraImageLoaded)
                                return;
                            //camera.lock();
                            Camera.Size r = camera.getParameters().getPreviewSize();

                            cameraBitmap = Bitmap.createBitmap(r.width, r.height, Bitmap.Config.ARGB_8888);
                            Allocation bmData = renderScriptNV21ToRGBA8888(getContext(), r.width, r.height, bytes);
                            bmData.copyTo(cameraBitmap);
                            camera.addCallbackBuffer(bytes);
                            //camera.unlock();
                            cameraImageLoaded = true;

                            //mPreview.refreshCamera(mCamera);
                        }
                    });

                //.refreshCamera(mCamera);
            }
        }, 100);
    }

    /**
     * Handle starting and stopping the roast timer
     */
    public void startStop(){
        //Bitmap bitmap = loadBitmapFromView(mPreview);
        if(stopped){
            checkpointButton.setText("Checkpoint");
            stopped = false;
            lastTime = System.nanoTime()/1000000000.0f;
        }else{
            checkpointButton.setText("Resume");
            stopped = true;
        }
    }

    /**
     * Function for acquiring bitmap from camera preview.
     * @param context
     * @param width
     * @param height
     * @param nv21
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Allocation renderScriptNV21ToRGBA8888(Context context, int width, int height, byte[] nv21) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        return out;
    }

    /**
     * Restarts camera
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        checkPermission();
    }

    /**
     * Releases camera
     */
    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    /**
     * Releases camera
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    /**
     * Updates the temperature if there isn't a large difference from the last.
     * @param newTemp
     */
    public void updateCurTemp(int newTemp){
        int tempDif = Math.abs((newTemp-lastTemp));

        lastTemp = newTemp;
        if(tempDif<20)
            curTemp.set(newTemp);
    }

    /**
     * Ask user through popup if they would like to save the current roast data.
     */
    public void promptForSave(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        saveRoast();
                        Toast.makeText(getContext(), "Roast data saved.", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Save roast data?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /**
     * Creates a RoastRecord object, adds it to database, then saves roast data to disk.
     */
    public void saveRoast(){
        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        RoastRecord record = new RoastRecord();
        record.name = "Test roast";
        record.startWeightPounds = 12.0f;
        record.endWeightPounds = 10.0f;
        record.dateTime = Calendar.getInstance().getTime().toString();
        record.roastProfile = roast;
        record.filename = record.roastProfile.name+" "+record.dateTime;
        record.setFilesizeBytes(checkpointTemps, safeTempsOverTime);
        record.id = db.addRoastRecord(record);

        Log.d("Database", "RoastRecord added with id:"+record.id);
        RoastRecord recordReturned = db.getRoastRecord(record.id);
        Log.d("Database", "RoastRecord returned: "+recordReturned.name);


        DataSaver.saveRoastData(record, safeTempsOverTime, checkpointTemps, getContext());
    }

    /**
     * Attempts to fix miss-recognized 9's as 5's
     */
    private void fixNine(){
        if(tempsOverTime.size()<3)
            return;

        int lastTemp = tempsOverTime.get(tempsOverTime.size()-3);
        int curTemp = tempsOverTime.get(tempsOverTime.size()-1);

        int lastTempHundreds = lastTemp/100;
        int lastTempTens = (lastTemp-lastTempHundreds*100)/10;
        int lastTempOnes = (lastTemp - lastTempHundreds*100 - lastTempTens*10);

        int curTempHundreds = curTemp/100;
        int curTempTens = (curTemp-curTempHundreds*100)/10;
        int curTempOnes = (curTemp - curTempHundreds*100 - curTempTens*10);


        if(curTempTens==5){
            if(lastTempTens==8||lastTempTens==9){
                curTempTens=9;
            }
        }

        if(curTempOnes==5){
            if(lastTempOnes==8||lastTempOnes==9){
                curTempOnes=9;
            }
        }


        int newTemp =curTempHundreds*100 + curTempTens*10 + curTempOnes;
        if(curTemp!=newTemp)
            Log.d("","");
        curTemp = newTemp;
        tempsOverTime.set(tempsOverTime.size()-1, curTemp);
    }
}