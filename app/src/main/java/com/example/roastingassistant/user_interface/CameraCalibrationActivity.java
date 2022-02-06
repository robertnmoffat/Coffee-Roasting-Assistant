package com.example.roastingassistant.user_interface;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import NeuralNetwork.NetworkController;
import NeuralNetwork.NeuralThread;
import Utilities.GlobalSettings;
import androidx.annotation.RequiresApi;

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
import android.widget.ImageView;
import android.widget.Toast;

import Camera.CameraPreview;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.roastingassistant.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Activity for calibrating the brightness setting used in the image recognition
 */
public class CameraCalibrationActivity extends AbstractCamera {
    int calibrationIteration = 0;
    final int TOTAL_CALIBRATIONS = 6;
    final int CALIBRATION_RESET = 6;

    Button[] calibrationChoicesButtons = new Button[TOTAL_CALIBRATIONS];
    float[] brightnessLevels = new float[TOTAL_CALIBRATIONS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityCompat.requestPermissions(CameraCalibrationActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, STORAGE_PERMISSION_CODE);
        setContentView(R.layout.activity_camera_calibration);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        calibrating = true;
        brightness = 0.0f;
        curTemp = new AtomicInteger();
        checkPermission();

        displayInfoPopup();

        networkController = new NetworkController(this);

        tempText = findViewById(R.id.cameracalibrationactivity_temp_textview);

        cameraPreview = findViewById(R.id.cameracalibrationactivity_camera_Layout);

        calibrationChoicesButtons[0] = findViewById(R.id.cameracalibrationactivity_output1_button);
        calibrationChoicesButtons[1] = findViewById(R.id.cameracalibrationactivity_output2_button);
        calibrationChoicesButtons[2] = findViewById(R.id.cameracalibrationactivity_output3_button);
        calibrationChoicesButtons[3] = findViewById(R.id.cameracalibrationactivity_output4_button);
        calibrationChoicesButtons[4] = findViewById(R.id.cameracalibrationactivity_output5_button);
        calibrationChoicesButtons[5] = findViewById(R.id.cameracalibrationactivity_output6_button);


        Button zoomInButton = findViewById(R.id.cameracalibrationactivity_zoomin_button);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int zoom = zoomIn();

                Log.d("zoomButton", "zoom in pressed. "+zoom);
            }
        });
        Button zoomOutButton = findViewById(R.id.cameracalibrationactivity_zoomout_button);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOut();
            }
        });


        Handler h = new Handler();
        int delay = 500; //milliseconds


        //Start handler to iterate through brightness settings and update buttons
        h.postDelayed(new Runnable(){
            public void run(){
                if(guessTextUpdated){
                    Log.d("Iteration", ""+calibrationIteration);

                    tempText.setText("Temperature:\n"+guessText);
                    //guessInt = Integer.parseInt(guessText);
                    guessTextUpdated=false;
                    if(calibrationIteration<TOTAL_CALIBRATIONS){
                        ImageView left = findViewById(R.id.cameracalibrationactivity_leftimg_imageview);
                        left.setImageBitmap(imageLeft);
                        ImageView mid = findViewById(R.id.cameracalibrationactivity_midimg_imageview);
                        mid.setImageBitmap(imageMid);
                        ImageView right = findViewById(R.id.cameracalibrationactivity_rightimg_imageview);
                        right.setImageBitmap(imageRight);
                        if (guessText.equals("No number found."))
                            calibrationChoicesButtons[calibrationIteration].setText("-");
                        else
                            calibrationChoicesButtons[calibrationIteration].setText(guessText);

                        calibrationChoicesButtons[calibrationIteration].setTextColor(Color.DKGRAY);
                        if(calibrationIteration>0)
                            calibrationChoicesButtons[calibrationIteration-1].setTextColor(Color.WHITE);
                        else
                            calibrationChoicesButtons[calibrationChoicesButtons.length-1].setTextColor(Color.WHITE);

                        final float finalBright = brightness;
                        calibrationChoicesButtons[calibrationIteration].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                GlobalSettings.getSettings(getContext()).setCameraBrightness(finalBright, getContext());
                                Toast.makeText(getContext(), "brightness:"+finalBright,Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });

                        brightnessLevels[calibrationIteration] = brightness;
                        Log.d("Iteration", "" + brightness);
                        brightness += 0.10;
                        calibrationIteration++;
                    }else if (calibrationIteration>=CALIBRATION_RESET){
                        if(brightness>=1.8)
                            brightness = 0.0f;
                        calibrationIteration = 0;
                    }else{
                        calibrationIteration++;
                    }

                }
                if(imageRightUpdated){
                    //graphView.setImageBitmap(imageLeft, imageMid, imageRight);
                    imageRightUpdated=false;
                }

                h.postDelayed(this, delay);
            }
        }, delay);

        //Check camera hardware permission and start camera
        checkPermission();

        //Start thread to run the image detection neural network.
        NeuralThread thread = new NeuralThread(this);
        thread.start();
    }

    /**
     * Release camera hardware when paused.
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
     * Get the context of this activity. (For inner anonymous classes to reference)
     * @return the context
     */
    public Context getContext(){
        return this;
    }

    /**
     * Check camera hardware permission is granted.
     * If it is, set up the camera preview.
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

    /**
     * Access camera hardware and get preview from it.
     * Start a handler to constantly check if the current image has been used up.
     * If it has, convert a new one from camera bytes to bitmap.
     */
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
        mCamera.startPreview();
        mPreview.refreshCamera(mCamera);

        //Start camera thread
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
                        cameraImageLoaded = true;
                    }
                });
            }
        }, 100);
    }

    /**
     * Function for acquiring bitmap from camera preview.
     * @param context activity context
     * @param width desired width
     * @param height desired height
     * @param nv21 camera image byte array
     * @return Image allocation
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
     * Reconnect the camera hardware
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        checkPermission();
    }

    /**
     * Release camera hardware
     */
    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    /**
     * Release camera hardware
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    /**
     * Update the current temperature value.
     * @param newTemp new temperature value
     */
    public void updateCurTemp(int newTemp){
        int tempDif = Math.abs((newTemp-lastTemp));

        lastTemp = newTemp;
        if(tempDif<20)
            curTemp.set(newTemp);
    }

    /**
     * Notify user to align camera with roaster when activity is started.
     */
    public void displayInfoPopup(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEUTRAL:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Align with roaster and select which button best matches the temperature on roaster.").setNeutralButton("Ok", dialogClickListener).show();
    }
}