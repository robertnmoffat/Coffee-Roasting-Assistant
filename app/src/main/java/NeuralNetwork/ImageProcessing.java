package NeuralNetwork;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class ImageProcessing {

    public static Bitmap filterBitmap(Bitmap bitmap, float brightness) {
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
                float dampening=1;
//                if(x<bitmap.getWidth()/3)
//                    dampening = (((float)x/(bitmap.getWidth()/3)));
//                else if(x>=bitmap.getWidth()/3&&x<=1+bitmap.getWidth()/3*2)
//                    dampening = 1;
//                else
//                    dampening = (((float)(bitmap.getWidth()-x)/(bitmap.getWidth()/3)));



                //if(dampening<1)dampening=1;

                int color = bitmap.getPixel(x, y);
                int red = (color & 0xff0000)>>16;
                int green = (color&0x00ff00)>>8;
                int blue = (color&0x0000ff)>>0;

                float total = 16+(65.738f*red/256f) + (129.057f*green/256f) + (25.064f*blue/256f);
                total *= brightness;
                // float total = red + blue + green-dampening;
                //total = total / 4.0f;
                total = (int)Math.round((total / filterValue));//int so that it rounds down

                int newColor = Color.argb(255, (int)(total * filterValue*dampening), (int)(total * filterValue*dampening), (int)(total * filterValue*dampening));

                bitmap.setPixel(x, y, newColor);
            }
        }


        //bitmap = applyFilter(bitmap, filter);

        Bitmap.Config conf = bitmap.getConfig(); // see other conf types
        Bitmap maxedBitmap = Bitmap.createBitmap(bitmap.getWidth()/2, bitmap.getHeight()/2, conf);

        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int color = bitmap.getPixel(x, y);
                int colorm = maxedBitmap.getPixel(x/2, y/2);
                int blue = (color&0x0000ff)>>0;
                int bluem = (colorm&0x0000ff)>>0;
                if(blue>=bluem)
                    maxedBitmap.setPixel(x/2,y/2,color);
            }
        }

        return maxedBitmap;
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

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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

    public static void SaveImage(Bitmap finalBitmap, String number) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File myDir = new File(root + "/guessNums/"+number);
        myDir.mkdirs();
        File[] files = myDir.listFiles();
        int count = files==null?0:files.length;

        String fname = ""+ count +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
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
