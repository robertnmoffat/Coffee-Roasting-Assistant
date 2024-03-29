package NeuralNetwork;

import android.graphics.Bitmap;
import android.os.Environment;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.AbstractCamera;
import com.example.roastingassistant.user_interface.CameraCalibrationActivity;
import com.example.roastingassistant.user_interface.RoastActivity;

import Database.Roast;

public class NeuralThread extends Thread {
    int calibrationIteration = 0;
    final int TOTAL_CALIBRATIONS = 6;
    int runCount = 0;
    int[][] guesses = new int[3][11];
    SingleDimension[] outputSums = new SingleDimension[3];
    int lastGuess=0;

    AbstractCamera roastActivity;

    public NeuralThread(RoastActivity roastActivity) {
        this.roastActivity = roastActivity;
    }
    public NeuralThread(CameraCalibrationActivity cameraCalibrationActivity){
        this.roastActivity = cameraCalibrationActivity;
    }

    public void run() {
        if(roastActivity==null)
            return;

        for(int i=0; i<outputSums.length; i++){
            outputSums[i] = new SingleDimension();
            outputSums[i].values = new float[11];
        }

        while(true) {
            Bitmap bm = roastActivity.getCameraBitmap();
            if (bm != null) {
                //bm = getResizedBitmap(bm, 400, 300);

                float previewWidth = roastActivity.getResources().getDimension(R.dimen.roastactivity_camerapreview_width);
                float previewHeight = roastActivity.getResources().getDimension(R.dimen.roastactivity_camerapreview_height);
                float highlightWidth = roastActivity.getResources().getDimension(R.dimen.roastactivity_hightlight_width);
                float highlightHeight = roastActivity.getResources().getDimension(R.dimen.roastactivity_hightlight_height);
                float widthPercent = highlightWidth / previewWidth;
                float heightPercent = highlightHeight / previewHeight;
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
                bm = Bitmap.createBitmap(bm, (int)((bmLeft-sideBuffer)*1.01), (int)((bmBottom-topBuffer)*1.01), width+sideBuffer+sideBuffer, height+topBuffer+topBuffer);//Add buffers twice to first counter the left and bottom buffer and hen to actual extend by that amount.
                Bitmap left = Bitmap.createBitmap(bm, 0, 0, (int) third+sideBuffer+sideBuffer, height+topBuffer+topBuffer);;
                Bitmap middle = Bitmap.createBitmap(bm, (int) (third), 0, (int) third+sideBuffer+sideBuffer, height+topBuffer+topBuffer);;
                Bitmap right = Bitmap.createBitmap(bm, (int) (third * 2), 0, (int) (third+sideBuffer+sideBuffer), height+topBuffer+topBuffer);
                int size = 64;

                left = ImageProcessing.getResizedBitmap(left, size, size);
                middle = ImageProcessing.getResizedBitmap(middle, size, size);
                right = ImageProcessing.getResizedBitmap(right, size, size);

                float brightness = roastActivity.getBrightness();

                left = ImageProcessing.filterBitmap(left, brightness);
                middle = ImageProcessing.filterBitmap(middle, brightness);
                right = ImageProcessing.filterBitmap(right, brightness);


                int translationDistance = 2;
                switch (runCount){
                    case 1:
                        left = ImageProcessing.translateBitmap(left, -translationDistance, 0);
                        middle = ImageProcessing.translateBitmap(middle, -translationDistance, 0);
                        right = ImageProcessing.translateBitmap(right, -translationDistance, 0);
                        break;
                    case 2:
                        left = ImageProcessing.translateBitmap(left, translationDistance, 0);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, translationDistance, 0);
                        right = ImageProcessing.translateBitmap(right, translationDistance, 0);
                        break;
                    case 3:
                        left = ImageProcessing.translateBitmap(left, 0, -translationDistance);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, 0, -translationDistance);
                        right = ImageProcessing.translateBitmap(right, 0, -translationDistance);
                        break;
                    case 4:
                        left = ImageProcessing.translateBitmap(left, 0, translationDistance);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, 0, translationDistance);
                        right = ImageProcessing.translateBitmap(right, 0, translationDistance);
                        break;
                    case 5:
                        translationDistance += 1;
                        left = ImageProcessing.translateBitmap(left, -translationDistance, 0);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, -translationDistance, 0);
                        right = ImageProcessing.translateBitmap(right, -translationDistance, 0);
                        break;
                    case 6:
                        translationDistance += 1;
                        left = ImageProcessing.translateBitmap(left, translationDistance, 0);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, translationDistance, 0);
                        right = ImageProcessing.translateBitmap(right, translationDistance, 0);
                        break;
                    case 7:
                        translationDistance += 1;
                        left = ImageProcessing.translateBitmap(left, 0, -translationDistance);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, 0, -translationDistance);
                        right = ImageProcessing.translateBitmap(right, 0, -translationDistance);
                        break;
                    case 8:
                        translationDistance += 1;
                        left = ImageProcessing.translateBitmap(left, 0, translationDistance);
                        if (lastGuess != 9)
                            middle = ImageProcessing.translateBitmap(middle, 0, translationDistance);
                        right = ImageProcessing.translateBitmap(right, 0, translationDistance);
                        break;
                }


                roastActivity.networkController.setBitmap(left);
                //long startTime = System.nanoTime();
                int leftGuess = roastActivity.networkController.getNumber();
                //long endTime = System.nanoTime();

                //long MethodeDuration = (endTime - startTime);
                guesses[0][leftGuess]++;
                addOutputs(outputSums[0], roastActivity.networkController.network.outputs);

                roastActivity.networkController.setBitmap(middle);
                int middleGuess = roastActivity.networkController.getNumber();
                lastGuess = middleGuess;
                guesses[1][middleGuess]++;
                addOutputs(outputSums[1], roastActivity.networkController.network.outputs);

                roastActivity.networkController.setBitmap(right);
                int guess = roastActivity.networkController.getNumber();
                guesses[2][guess]++;
                addOutputs(outputSums[2], roastActivity.networkController.network.outputs);

                String path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();


                if(runCount==8) {
                    float[] highestOutput = new float[]{-9999.0f,-9999.0f,-9999.0f};
                    int[] highestOutputPos = new int[]{-1,-1,-1};

                    int[] highestCount = new int[]{0,0,0};
                    int[] position = new int[]{-1,-1,-1};
                    for(int i=0; i<11; i++){
                        for(int p=0; p<3; p++) {
                            if(outputSums[p].values[i]>highestOutput[p]){
                                highestOutput[p] = outputSums[p].values[i];
                                highestOutputPos[p] = i;
                            }

                            if (guesses[p][i] > highestCount[p]) {
                                position[p] = i;
                                highestCount[p] = guesses[p][i];
                            }
                        }
                    }
                    if(roastActivity.guessTextUpdated==false) {
                        if(roastActivity.networkController.getErrorEstimate()<900.0f) {
                            int temp = (position[0] * 100 + position[1] * 10 + position[2]);
                            int sumTemp = (highestOutputPos[0] * 100 + highestOutputPos[1] * 10 + highestOutputPos[2]);

                            float nnTotal = getTotal(roastActivity.networkController);
                            roastActivity.guessText = "" + sumTemp;//temp+" sums: "+sumTemp+" total:"+nnTotal;//+" "+roastActivity.networkController.getErrorEstimate();
                            if(highestOutputPos[0]==10||highestOutputPos[1]==10||highestOutputPos[2]==10) {
                                roastActivity.guessText = "No number found.";
                            }else {
                                roastActivity.updateCurTemp(sumTemp);
                            }
                        }
                        else
                            roastActivity.guessText = "no number";
                        roastActivity.guessTextUpdated=true;
                    }
                    runCount=0;
                    guesses = new int[3][11];
                    clearOutputSums();
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

    public float getTotal(NetworkController nController){
        float total = 0.0f;
        for(int i=0; i<11; i++){
            total+=nController.getOutput(i);
        }
        return total;
    }

    public void addOutputs(SingleDimension sums, SingleDimension toAdd){
        for(int i=0; i<sums.values.length; i++){
            sums.values[i] += toAdd.values[i];
        }
    }

    public void clearOutputSums(){
        for(int i=0; i<outputSums.length; i++){
            outputSums[i].values = new float[11];
        }
    }
}
