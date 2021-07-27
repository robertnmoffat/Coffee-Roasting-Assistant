package NeuralNetwork;

import android.graphics.Bitmap;
import android.os.Environment;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.RoastActivity;

public class NeuralThread extends Thread {
    int runCount = 0;
    int[][] guesses = new int[3][10];

    RoastActivity roastActivity;

    public NeuralThread(RoastActivity roastActivity) {
        this.roastActivity = roastActivity;
    }

    public void run() {
        while(true) {
            Bitmap bm = roastActivity.getCameraBitmap();
            if (bm != null) {
                //bm = getResizedBitmap(bm, 400, 300);

                float previewWidth = roastActivity.getResources().getDimension(R.dimen.roastactivity_camerapreview_width);
                float previewHeight = roastActivity.getResources().getDimension(R.dimen.roastactivity_camerapreview_height);
                float hightlightWidth = roastActivity.getResources().getDimension(R.dimen.roastactivity_hightlight_width);
                float hightlightHeight = roastActivity.getResources().getDimension(R.dimen.roastactivity_hightlight_height);
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
                float bufferSize = 6+runCount;
                int sideBuffer = (int)(third/bufferSize);
                int topBuffer = (int)(height/bufferSize);


                //bm = getBitmapSubsection(bm, bmLeft,bmTop,bmRight,bmBottom);
                bm = Bitmap.createBitmap(bm, bmLeft-sideBuffer, bmBottom-topBuffer, width+sideBuffer+sideBuffer, height+topBuffer+topBuffer);//Add buffers twice to first counter the left and bottom buffer and hen to actual extend by that amount.
                Bitmap left = Bitmap.createBitmap(bm, 0, 0, (int) third+sideBuffer+sideBuffer, height+topBuffer+topBuffer);;
                Bitmap middle = Bitmap.createBitmap(bm, (int) (third), 0, (int) third+sideBuffer+sideBuffer, height+topBuffer+topBuffer);;
                Bitmap right = Bitmap.createBitmap(bm, (int) (third * 2), 0, (int) (third+sideBuffer+sideBuffer), height+topBuffer+topBuffer);
                int size = 64;
                left = ImageProcessing.getResizedBitmap(left, size, size);
                middle = ImageProcessing.getResizedBitmap(middle, size, size);
                right = ImageProcessing.getResizedBitmap(right, size, size);

                left = ImageProcessing.filterBitmap(left);
                middle = ImageProcessing.filterBitmap(middle);
                right = ImageProcessing.filterBitmap(right);
                roastActivity.networkController.setBitmap(left);
                int leftGuess = roastActivity.networkController.getNumber();
                guesses[0][leftGuess]++;
                roastActivity.networkController.setBitmap(middle);
                int middleGuess = roastActivity.networkController.getNumber();
                guesses[1][middleGuess]++;
                roastActivity.networkController.setBitmap(right);
                int guess = roastActivity.networkController.getNumber();
                guesses[2][guess]++;

                String path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();

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
                    if(roastActivity.guessTextUpdated==false) {
                        if(roastActivity.networkController.getErrorEstimate()<900.0f) {
                            roastActivity.guessText = "" + (position[0] * 100 + position[1] * 10 + position[2]);//+" "+roastActivity.networkController.getErrorEstimate();
                            roastActivity.curTemp = (position[0] * 100 + position[1] * 10 + position[2]);
                        }
                        else
                            roastActivity.guessText = "no number";
                        roastActivity.guessTextUpdated=true;
                    }
                    runCount=0;
                    guesses = new int[3][10];
                }else {
                    runCount++;
                }


                if(roastActivity.imageRightUpdated==false) {
                    roastActivity.imageLeft = left;
                    roastActivity.imageMid = middle;
                    roastActivity.imageRight = right;
                    roastActivity.imageRightUpdated=true;
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
