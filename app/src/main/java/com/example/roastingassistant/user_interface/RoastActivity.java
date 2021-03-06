package com.example.roastingassistant.user_interface;

import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;
import Database.RoastRecord;
import NeuralNetwork.NetworkController;
import NeuralNetwork.NeuralThread;
import Utilities.DataCleaner;
import Utilities.DataSaver;
import Utilities.Utilities;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

public class RoastActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;

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
    TextView tempText;
    public int curTemp;
    int lastTemp;

    LinearLayout checkpointsLayout;
    LinearLayout cameraPreview;

    float currentTime = 0.0f;
    float lastTime;
    String timeString;

    ArrayList<Integer> tempsOverTime;
    ArrayList<Integer> safeTempsOverTime;
    ArrayList<Integer> checkpointTemps;

    byte[] cameraData;
    Bitmap cameraBitmap;

    boolean cameraImageLoaded = false;
    public Bitmap imageLeft, imageMid, imageRight;
    public boolean imageRightUpdated=false;
    public String guessText="";
    public boolean guessTextUpdated=false;

    public NetworkController networkController;

    int STORAGE_PERMISSION_CODE = 100;

    MediaPlayer player;

    Roast roast;
    int currentCheckpoint = 0;

    SoundPool soundPool;
    int soundEffect1, soundEffect2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build();
//            soundPool = new SoundPool.Builder()
//                    .setMaxStreams(6)
//                    .setAudioAttributes(audioAttributes)
//                    .build();
//        } else {
//            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
//        }

        player = MediaPlayer.create(this,
                R.raw.ring);


        if(testing){
            testTemps = new ArrayList<>();
            RoastRecord record = new RoastRecord();
            record.filename="TestData";
            record.filesizeBytes = 15040;
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
                //EditText et = findViewById(R.id.roastactivity_num_edittext);
                //String num = et.getText().toString();
                //String leftNum = String.valueOf(num.charAt(0));
                //String midNum = String.valueOf(num.charAt(1));
                //String rightNum = String.valueOf(num.charAt(2));

                //ImageProcessing.SaveImage(imageLeft, leftNum);
                //ImageProcessing.SaveImage(imageMid, midNum);
                //ImageProcessing.SaveImage(imageRight, rightNum);
                DatabaseHelper db = DatabaseHelper.getInstance(getContext());
                RoastRecord record = new RoastRecord();
                record.name = "Test roast";
                record.startWeightPounts = 12.0f;
                record.endWeightPounds = 10.0f;
                record.dateTime = Calendar.getInstance().getTime().toString();
                record.roastProfile = roast;
                record.filename = record.roastProfile.name+" "+record.dateTime;
                record.filesizeBytes = (checkpointTemps.size()+safeTempsOverTime.size()+2)*Integer.BYTES;
                record.id = db.addRoastRecord(record);

                Log.d("Database", "RoastRecord added with id:"+record.id);
                RoastRecord recordReturned = db.getRoastRecord(record.id);
                Log.d("Database", "RoastRecord returned: "+recordReturned.name);


                DataSaver.saveRoastData(record, safeTempsOverTime, checkpointTemps, getContext());
                String[] filenames = getContext().fileList();
                for(int i=0;i<filenames.length;i++) {
                    Log.d("FileIO", "" + filenames[i]);
                }
                ArrayList<Integer> temps = new ArrayList<>();
                ArrayList<Integer> checks = new ArrayList<>();
                DataSaver.loadRoastData(record, temps, checks, getContext());

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
                    tempText.setText(guessText);
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
                    String secString = String.format("%.1f", secs);
                    timeString = ""+mins+":"+secString;
                    timeText.setText("Time:" + timeString);
                    lastTime = System.nanoTime()/1000000000.0f;

                    String num = guessText;
                    //String leftNum = String.valueOf(num.charAt(0));
                    //String midNum = String.valueOf(num.charAt(1));
                    //String rightNum = String.valueOf(num.charAt(2));

                    //ImageProcessing.SaveImage(imageLeft, leftNum);
                    //ImageProcessing.SaveImage(imageMid, midNum);
                    //ImageProcessing.SaveImage(imageRight, rightNum);

                    //if((int)(currentTime/1000000000)%2==0) {

                    recordSafeTemp();

                    if (testing) {
                        if(testPos<testTemps.size()) {
                            curTemp = testTemps.get(testPos);
                            guessText=""+curTemp;
                            guessTextUpdated=true;
                            tempsOverTime.add(testTemps.get(testPos-1));
                            tempsOverTime.add(testTemps.get(testPos));
                            testPos+=2;
                        }
                    } else {
                        tempsOverTime.add((int) currentTime);
                        tempsOverTime.add(curTemp);
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
                    //graphView.addTemp(testTemp);
                    //}
                    //if(tempsOverTime.size()<(int)(currentTime/1000000000))
                    //tempsOverTime.add(guessInt);
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

    public int lastSafeTemp(){
        if(safeTempsOverTime.size()>0)
            return safeTempsOverTime.get(safeTempsOverTime.size()-1);
        return curTemp;
    }

    public void triggerCheckpoint(){
        checkpointWarned = false;
        if(currentCheckpoint<roast.checkpoints.size()) {
            checkpointTemps.add((int) currentTime);
            if(safeTempsOverTime.size()!=0)
                checkpointTemps.add(safeTempsOverTime.get(safeTempsOverTime.size() - 1));
            else
                checkpointTemps.add(0);
        }
        currentCheckpoint++;
        if(currentCheckpoint<roast.checkpoints.size()) {
            updateCheckpointText();
        }else{
            checkpointText.setText("Roast complete.");
        }
    }

    public void recordSafeTemp(){
        safeTempsOverTime = DataCleaner.medianFilter(tempsOverTime, 100);


//        int avgLen = 10;
//        if(tempsOverTime.size()-avgLen-1>0) {
//            float tempAvg = 0.0f;
//
//            for(int i=0; i<avgLen; i++){
//                tempAvg+=(float)tempsOverTime.get(tempsOverTime.size() - avgLen+i)-tempsOverTime.get(tempsOverTime.size() - avgLen+i-1);
//            }
//            tempAvg/=(float)tempsOverTime.size();
//            int curTempVel = (curTemp - tempsOverTime.get(tempsOverTime.size() - 1));
//            Log.d("tempVelocity", " avg:"+tempAvg+" cur:" + curTempVel);
//
//            if(Math.abs(curTempVel)<5&&Math.sqrt(tempAvg*tempAvg)<3){
//                safeTempsOverTime = DataCleaner.medianFilter(tempsOverTime, 100);
////                safeTempsOverTime.add((int)currentTime);
////                safeTempsOverTime.add(curTemp);
//            }else{
//                Log.d("tempVelocity", "SKIPPED");
//            }
//        }
    }

    public void updateCheckpointText(){
        if(roast!=null) {
            if (roast.checkpoints.size() > 0&&currentCheckpoint<roast.checkpoints.size()) {
                if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.Temperature)
                    checkpointText.setText(roast.checkpoints.get(currentCheckpoint).name + " at " + roast.checkpoints.get(currentCheckpoint).temperature + " degrees.");
                if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.Time)
                    checkpointText.setText(roast.checkpoints.get(currentCheckpoint).name + " at " + Utilities.secondsToTimeString(roast.checkpoints.get(currentCheckpoint).timeTotalInSeconds()));
                if (roast.checkpoints.get(currentCheckpoint).trigger == Checkpoint.trig.PromptAtTemp)
                    checkpointText.setText(roast.checkpoints.get(currentCheckpoint).name + " at " + roast.checkpoints.get(currentCheckpoint).temperature);
            }
        }
    }




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

    public Context getContext(){
        return this;
    }

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
        mPicture = getPictureCallback();
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

    private Camera.PictureCallback getPictureCallback(){
        return new Camera.PictureCallback(){

            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                cameraData = bytes;
                mPreview.refreshCamera(mCamera);
            }
        };
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }


    public void updateCurTemp(int newTemp){
        int tempDif = Math.abs((newTemp-lastTemp));

        lastTemp = newTemp;
        if(tempDif<20)
            curTemp = newTemp;
    }
}