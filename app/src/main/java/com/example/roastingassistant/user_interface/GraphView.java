package com.example.roastingassistant.user_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class GraphView extends View {
    Bitmap left, mid, right;
    ArrayList<Integer> temps;
    int lowestTemp = 180;
    int lowestTempPos = 0;
    int highestTemp = 460;
    int highestTempPos = 0;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = canvas.getWidth(), h = canvas.getHeight();

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLACK);
        canvas.drawLine(0.1f*w, 0.9f*h, 0.95f*w, 0.9f*h, paint);
        canvas.drawLine(0.1f*w, 0.05f*h, 0.1f*w, 0.9f*h, paint);

        if(temps!=null&& temps.size()>1){
            int range = highestTemp-lowestTemp;

            float incrementor = 1;
            Log.d("TestTemp", ""+(temps.size()*4)+">"+(w*0.25f));
            if(temps.size()*4>w*0.25f) {
                incrementor = ((float) (temps.size() * 4) / (w * 0.25f));
                Log.d("TestTemp", "OVER");
            }

            /*
            Path p = new Path();
            p.moveTo(a1[0].x, a1[0].y);
            for (int i = 1; i < x.length; i++) {
                p.lineTo(a1[i].x, a1[i].y);
            }
            _canvas.drawPath(p, _paint);
             */
            Path p = new Path();
            float size = temps.size();
            float[] points = new float[(int)(size/incrementor)*4];
            int pointPos = 0;

            for(float i=1; i<temps.size(); i+=incrementor){
                    float prevTemp = temps.get((int)(i-incrementor));
                    float curTemp = temps.get((int)i);

                    float firstPos = prevTemp/highestTemp;
                    float secondPos = curTemp/highestTemp;
                    firstPos *= (float)h*0.9f;
                    secondPos *= (float)h*0.9f;


                    float firstX = ((i-incrementor)/size)*(w*0.8f);
                    float secondX = ((i)/size)*(w*0.8f);
                    //canvas.drawLine(firstX, firstPos ,secondX, secondPos, paint);
                    p.lineTo(secondX, secondPos+40);
                    points[pointPos++]=firstX;
                    points[pointPos++]=firstPos;
                    points[pointPos++]=secondX;
                    points[pointPos++]=secondPos;
            }
            canvas.drawLines(points, paint);
            //canvas.drawPath(p, paint);
        }

        if(left!=null) {
            Matrix matrix = new Matrix();
            matrix.postScale(4, 4);

            canvas.drawBitmap(left, matrix, paint);
            matrix.postTranslate(130,0);
            canvas.drawBitmap(mid, matrix, paint);
            matrix.postTranslate(130,0);
            canvas.drawBitmap(right, matrix, paint);
        }

        canvas.save();
    }

    public void setImageBitmap(Bitmap imageleft, Bitmap imagemid, Bitmap imageRight) {
        left = imageleft;
        mid = imagemid;
        right = imageRight;

        this.invalidate();
    }

    public void addTemp(int temp){
        if(temps==null)
            temps = new ArrayList<>();

        if(temp<lowestTemp){
            lowestTemp=temp;
            lowestTempPos = temps.size();
        }else if(temp>highestTemp){
            highestTemp=temp;
            highestTempPos = temps.size();
        }
        temps.add(temp);
    }
}
