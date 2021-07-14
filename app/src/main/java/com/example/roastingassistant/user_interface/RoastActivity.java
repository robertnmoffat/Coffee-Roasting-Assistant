package com.example.roastingassistant.user_interface;

import NeuralNetwork.ConvolutionalNeuralNetwork;
import NeuralNetwork.NetworkController;
import NeuralNetwork.NetworkFileLoader;
import NeuralNetwork.NetworkInitializer;
import NeuralNetwork.Square;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import android.text.Html;
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

import static com.example.roastingassistant.user_interface.Utils.dp;

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
    TextView tempText;

    LinearLayout checkpointsLayout;
    LinearLayout cameraPreview;

    float currentTime = 0.0f;
    float lastTime;
    String timeString;


    byte[] cameraData;
    Bitmap cameraBitmap;

    boolean imageCollectionStarted = false;
    boolean cameraImageLoaded = false;
    Bitmap imageRight;
    boolean imageRightUpdated=false;
    String guessText="";
    boolean guessTextUpdated=false;

    NetworkController networkController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkPermission();

        networkController = new NetworkController(this);

        stopButton = findViewById(R.id.roastactivity_stop_button);
        undoButton = findViewById(R.id.roastactivity_undocheckpoint_button);
        checkpointButton = findViewById(R.id.roastactivity_checkpoint_button);
        timeText = findViewById(R.id.roastactivity_time_textview);
        checkpointsLayout = findViewById(R.id.roastactivity_checkpoint_linearlayout);
        tempText = findViewById(R.id.roastactivity_temp_textview);

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
                if(guessTextUpdated){
                    tempText.setText(guessText);
                    guessTextUpdated=false;
                }
                if(imageRightUpdated){
                    graphImage.setImageBitmap(imageRight);
                    imageRightUpdated=false;
                }

                if(!stopped) {

                    currentTime += System.nanoTime()-lastTime;
                    timeString = String.format("%.1f", currentTime/1000000000);
                    timeText.setText("Time:" + timeString);
                    lastTime = System.nanoTime();
                }
                h.postDelayed(this, delay);
            }
        }, delay);

        NeuralThread thread = new NeuralThread();
        thread.start();

//        Handler h2 = new Handler();
//        int delay2 = 250; //milliseconds


        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inScaled = false;
        //Bitmap four = BitmapFactory.decodeResource(getResources(), R.drawable.four, options);
        //Bitmap eight = BitmapFactory.decodeResource(getResources(), R.drawable.eight, options);
        //Bitmap nine = BitmapFactory.decodeResource(getResources(), R.drawable.nine, options);
        //Bitmap seven = BitmapFactory.decodeResource(getResources(), R.drawable.seven, options);
        //Bitmap three = BitmapFactory.decodeResource(getResources(), R.drawable.three, options);
        //Bitmap five = BitmapFactory.decodeResource(getResources(), R.drawable.five, options);

//        h2.postDelayed(new Runnable(){
//
//
//            public void run(){
//                tempText.setText("Current Temp:"+networkController.temperature);
//
//
//                //bm = Bitmap.createScaledBitmap(bm, 400, 300, false);
////                networkController.setBitmap(five);
////                int num = networkController.getNumber();
////
////                networkController.setBitmap(four);
////                num = networkController.getNumber();
////                networkController.setBitmap(eight);
////                num = networkController.getNumber();
////                networkController.setBitmap(nine);
////                num = networkController.getNumber();
////                networkController.setBitmap(seven);
////                num = networkController.getNumber();
////                networkController.setBitmap(three);
////                num = networkController.getNumber();
//
//
//
//
//
//                h2.postDelayed(this, delay2);
//            }
//        }, delay2);

        checkPermission();

    }

    public static Bitmap filterBitmap(Bitmap bitmap) {
        float filterValue = 255 / 1;

        Square filter = new Square();
        filter.width = 3;
        filter.values = new float[][]{{0,1,0},
                                      {0,1,0},
                                      {0,1,0}};



        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int dampening = (int) ((x-(bitmap.getWidth()/2))*(x-(bitmap.getWidth()/2)));
                if(dampening<1)dampening=1;

                int color = bitmap.getPixel(x, y);
                int red = (color & 0xff0000)>>16;
                int green = (color&0x00ff00)>>8;
                int blue = (color&0x0000ff)>>0;

                float total = 16+(65.738f*red/256f) + (129.057f*green/256f) + (25.064f*blue/256f);
                // float total = red + blue + green-dampening;
                //total = total / 4.0f;
                total = (int)Math.round((total / filterValue));
                int newColor = Color.argb(255, (int)(total * filterValue), (int)(total * filterValue), (int)(total * filterValue));

                bitmap.setPixel(x, y, newColor);
            }
        }

        bitmap = applyFilter(bitmap, filter);

        return bitmap;
    }

    private Bitmap getBitmapSubsection(Bitmap bitmap, int left, int top, int right, int bottom) {
        Bitmap bmOverlay = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(left, top, right, bottom, paint);

        return bmOverlay;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
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

    class NeuralThread extends Thread {
        int runCount = 0;
        int[][] guesses = new int[3][10];

        NeuralThread() {
        }

        public void run() {
            while(true) {
                Bitmap bm = getCameraBitmap();
                if (bm != null) {
                    //bm = getResizedBitmap(bm, 400, 300);

                    float previewWidth = getResources().getDimension(R.dimen.roastactivity_camerapreview_width);
                    float previewHeight = getResources().getDimension(R.dimen.roastactivity_camerapreview_height);
                    float hightlightWidth = getResources().getDimension(R.dimen.roastactivity_hightlight_width);
                    float hightlightHeight = getResources().getDimension(R.dimen.roastactivity_hightlight_height);
                    float widthPercent = hightlightWidth / previewWidth;
                    float heightPercent = hightlightHeight / previewHeight;
                    float bmSelectionWidth = (bm.getWidth() * widthPercent);
                    float bmSelectionHeight = (bm.getHeight() * heightPercent);
                    int bmLeft = (int) ((bm.getWidth() / 2) - (bmSelectionWidth / 2));
                    int bmRight = (int) ((bm.getWidth() / 2) + (bmSelectionWidth / 2));
                    int bmTop = (int) ((bm.getHeight() / 2) + (bmSelectionHeight / 2));
                    int bmBottom = (int) ((bm.getHeight() / 2) - (bmSelectionHeight / 2));

                    int width = bmRight - bmLeft;
                    int height = bmTop - bmBottom;
                    float third = width / 3;


                    //bm = getBitmapSubsection(bm, bmLeft,bmTop,bmRight,bmBottom);
                    bm = Bitmap.createBitmap(bm, bmLeft, bmBottom, width, height);
                    Bitmap left = Bitmap.createBitmap(bm, 0, 0, (int) third, height);;
                    Bitmap middle = Bitmap.createBitmap(bm, (int) (third), 0, (int) third, height);;
                    Bitmap right = Bitmap.createBitmap(bm, (int) (third * 2), 0, (int) third, height);
                    left = getResizedBitmap(left, 32, 32);
                    middle = getResizedBitmap(middle, 32, 32);
                    right = getResizedBitmap(right, 32, 32);
                    left = filterBitmap(left);
                    middle = filterBitmap(middle);
                    right = filterBitmap(right);
                    networkController.setBitmap(left);
                    int leftGuess = networkController.getNumber();
                    guesses[0][leftGuess]++;
                    networkController.setBitmap(middle);
                    int middleGuess = networkController.getNumber();
                    guesses[1][middleGuess]++;
                    networkController.setBitmap(right);
                    int guess = networkController.getNumber();
                    guesses[2][guess]++;

                    if(runCount==6) {
                        int[] highestCount = new int[]{0,0,0};
                        int[] position = new int[]{-1,-1,-1};
                        for(int i=0; i<10; i++){
                            for(int p=0; p<3; p++) {
                                if (guesses[p][i] > highestCount[p]) {
                                    position[p] = i;
                                    highestCount[p] = guesses[p][i];
                                }
                            }
                        }
                        if(guessTextUpdated==false) {
                            if(networkController.getErrorEstimate()<900.0f)
                                guessText = "" + (position[0]*100+position[1]*10+position[2])+" erest:"+networkController.getErrorEstimate();
                            else
                                guessText = "no number";
                            guessTextUpdated=true;
                        }
                        runCount=0;
                        guesses = new int[3][10];
                    }else {
                        runCount++;
                    }


                    if(imageRightUpdated==false) {
                        imageRight = right;
                        imageRightUpdated=true;
                    }

                    bm.recycle();


                }
                try {
                    Thread.sleep(125);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static public Bitmap applyFilter(Bitmap input, Square filter)
    {
        int width = input.getWidth(), height = input.getHeight();

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap filteredBitmap = Bitmap.createBitmap(width, height, conf);


        float positionSum = 0.0f;
        for (int posy = 0; posy < width; posy++)
        {
            for (int posx = 0; posx < height; posx++)
            {
                positionSum = 0.0f;

                for (int y = 0; y < filter.width; y++)
                {
                    for (int x = 0; x < filter.width; x++)
                    {
                        int desiredx = posx+x-filter.width/2;
                        int desiredy = posy+y-filter.width/2;
                        if(desiredx<0||desiredx>=width||desiredy<0||desiredy>=height)
                            continue;
                        float red = (input.getPixel(desiredx,desiredy)&0xff0000)>>16;
                        float green = (input.getPixel(desiredx,desiredy)&0x00ff00)>>8;
                        float blue = (int)(input.getPixel(desiredx,desiredy)&0x0000ff);
                        if(blue>0)
                            Log.e("sdf","asdf");
                        float filt = filter.values[x][y];//just grab one colour. it's black and white so they are all the same.
                        positionSum += blue * filt;
                    }
                }
                int brightness = ((int)positionSum)>=200?255:0;
                filteredBitmap.setPixel(posx,posy, Color.argb(255,brightness,brightness,brightness));
            }
        }
        return filteredBitmap;
    }
}