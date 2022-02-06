package com.example.roastingassistant.user_interface;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaPlayer;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roastingassistant.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import NeuralNetwork.NetworkController;
import NeuralNetwork.NeuralThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import Camera.CameraPreview;

/**
 * Class to be extended by any activities utilizing the camera hardware.
 */
public abstract class AbstractCamera extends AppCompatActivity {
    protected float brightness = 0.75f;
    public boolean calibrating = false;//If being used by the CameraCalibration class

    protected Camera mCamera;
    protected CameraPreview mPreview;

    protected TextView tempText;
    public AtomicInteger curTemp;
    protected int lastTemp;

    LinearLayout checkpointsLayout;
    LinearLayout cameraPreview;

    byte[] cameraData;
    Bitmap cameraBitmap;

    boolean cameraImageLoaded = false;
    public Bitmap imageLeft, imageMid, imageRight;
    public boolean imageRightUpdated=false;
    public String guessText="";
    public boolean guessTextUpdated=false;

    public NetworkController networkController;

    int STORAGE_PERMISSION_CODE = 100;

    /**
     * Get bitmap of camera preview
     * @return returns new bitmap, or null if a new one has not yet been generated
     */
    public abstract Bitmap getCameraBitmap();

    /**
     * Method called by the NeuralThread to update temperature in the activity.
     * @param temp Updated temperature
     */
    public abstract void updateCurTemp(int temp);

    /**
     * For neuralThread to access the brightness setting from the activity to be used in image preprocessing.
     * @return The brightness value
     */
    public float getBrightness(){
        return brightness;
    }
}
