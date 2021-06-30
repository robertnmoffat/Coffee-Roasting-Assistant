package com.example.roastingassistant.user_interface;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roastingassistant.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import Camera.CameraPreview;

import java.util.List;

public class RoastActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;

    boolean stopped=true;
    Button stopButton;
    Button undoButton;
    Button checkpointButton;
    ImageView graphImage;

    TextView timeText;

    LinearLayout checkpointsLayout;
    LinearLayout cameraPreview;

    float currentTime = 0.0f;
    float lastTime;
    String timeString;

    byte[] cameraData;
    Bitmap cameraBitmap;

    boolean imageCollectionStarted = false;
    boolean cameraImageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkPermission();

        stopButton = findViewById(R.id.roastactivity_stop_button);
        undoButton = findViewById(R.id.roastactivity_undocheckpoint_button);
        checkpointButton = findViewById(R.id.roastactivity_checkpoint_button);
        timeText = findViewById(R.id.roastactivity_time_textview);
        checkpointsLayout = findViewById(R.id.roastactivity_checkpoint_linearlayout);

        graphImage = findViewById(R.id.roastactivity_graph_imageView);
        cameraPreview = findViewById(R.id.roastactivity_camera_Layout);

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

        checkPermission();

    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();
    }

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

    private void setupPreview(){
        mCamera = Camera.open();
        mPreview = new CameraPreview(getBaseContext(), mCamera);


        try{
            //try to set camera focus
            Camera.Parameters params = mCamera.getParameters();
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

    public void startStop(){
        //Bitmap bitmap = loadBitmapFromView(mPreview);
        if(stopped){
            checkpointButton.setText("Checkpoint");
            stopped = false;
            lastTime = System.nanoTime();
        }else{
            checkpointButton.setText("Resume");
            stopped = true;
        }
    }

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
}