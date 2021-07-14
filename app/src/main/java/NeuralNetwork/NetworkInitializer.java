package NeuralNetwork;

import android.graphics.Bitmap;
import android.graphics.Color;
import NeuralNetwork.Square;
import NeuralNetwork.SquareLayer;
import NeuralNetwork.SingleDimension;

import java.util.Random;

public class NetworkInitializer {
    static Random rnd;

    static int firstFilterLayerCount = 6;
    static int firstFilterWidth = 5;

    static int firstConvolutionWidth = 28;

    static int firstDownsampleWidth = 14;

    //static int secondFilterCount = 59;
    static int secondFilterWidth = 5;
    static int secondConvolutionCount = 16;
    static int secondFilterCount = firstFilterLayerCount * secondConvolutionCount;

    static int secondConvolutionWidth = 10;

    static int secondDownsampleWidth = 5;

    static int hiddenLayerCount = 2;
    static int firstHiddenLayerNeuronCount = 120;
    static int secondHiddenLayerNeuronCount = 100;

    static int firstWeightCount = secondDownsampleWidth * secondDownsampleWidth * //amount of pixels per downsampled image
            secondConvolutionCount * //amount of downsampled images
            firstHiddenLayerNeuronCount; //amount of neurons in next layer

    static int secondWeightCount = firstHiddenLayerNeuronCount * secondHiddenLayerNeuronCount;//amount of neurons in first layer multiplied by amount in second layer

    static int outputNeuronCount = 10;

    static int thirdWeightCount = secondHiddenLayerNeuronCount * outputNeuronCount;//amount of neurons in last layer multiplied by amount of output neurons

    /**
     * Converts a two dimensional bitmap image to a one dimensional input suitable for the neural network.
     * @param network The network to set the input neurons in.
     * @param input The bitmap to be converted to inputs.
     */
    public static void setInputs(ConvolutionalNeuralNetwork network, Bitmap input) {
        network.input = new Square();
        network.input.width = input.getWidth();
        network.input.values = new float[input.getWidth()][input.getHeight()];

        for (int y=0; y<network.input.width; y++) {
            for (int x=0; x<network.input.width; x++) {
                int color = input.getPixel(x, y);
                int red = (color & 0xff0000)>>16;
                int green = (color&0x00ff00)>>8;
                int blue = (color&0x0000ff)>>0;
                float gray = (red + green + blue) / 3;//average all three colour parameters to get greyscale

                network.input.values[x][y] = (gray/255)*2-1;//convert to percentage of full colour so that it is value between 1 and 0
            }
        }
    }

    /**
     * Initializes all of the structures of the network according to what is represented in the saved neural net.
     * @param network An initialized untrained neural network.
     */
    public static void InitializeNetwork(ConvolutionalNeuralNetwork network){
        network.filterLayers = new SquareLayer[2];
        network.convolutedLayers = new SquareLayer[2];
        network.activatedConvolutedLayers = new SquareLayer[2];

        //First filters
        network.filterLayers[0] = new SquareLayer();
        network.filterLayers[0].squares = new Square[firstFilterLayerCount];//Amount of filters
        for (int i = 0; i < firstFilterLayerCount; i++)
        {
            network.filterLayers[0].squares[i] = new Square();
            network.filterLayers[0].squares[i].width = firstFilterWidth;//width of square filter
            network.filterLayers[0].squares[i].values = new float[firstFilterWidth][firstFilterWidth];
        }

        //convolved post filter
        network.convolutedLayers[0] = new SquareLayer();
        network.convolutedLayers[0].squares = new Square[firstFilterLayerCount];//amount of convolved images
        network.activatedConvolutedLayers[0] = new SquareLayer();
        network.activatedConvolutedLayers[0].squares = new Square[firstFilterLayerCount];//amount of convolved images
        for (int i = 0; i < firstFilterLayerCount; i++)
        {
            network.convolutedLayers[0].squares[i] = new Square();
            network.convolutedLayers[0].squares[i].width = firstConvolutionWidth;//width of square convolved images
            network.convolutedLayers[0].squares[i].values = new float[firstConvolutionWidth][firstConvolutionWidth];
            network.convolutedLayers[0].squares[i].biases = new float[firstConvolutionWidth][firstConvolutionWidth];

            network.activatedConvolutedLayers[0].squares[i] = new Square();
            network.activatedConvolutedLayers[0].squares[i].width = firstConvolutionWidth;//width of square convolved images
            network.activatedConvolutedLayers[0].squares[i].values = new float[firstConvolutionWidth][firstConvolutionWidth];
            network.activatedConvolutedLayers[0].squares[i].biases = new float[firstConvolutionWidth][firstConvolutionWidth];
        }

        //first downsample
        network.downsampledLayers = new SquareLayer[2];
        network.downsampledLayers[0] = new SquareLayer();
        network.downsampledLayers[0].squares = new Square[firstFilterLayerCount];//amount of downsampled images
        for (int i = 0; i < firstFilterLayerCount; i++) {
            network.downsampledLayers[0].squares[i] = new Square();
            network.downsampledLayers[0].squares[i].width = firstDownsampleWidth;//width of square downsampled images
            network.downsampledLayers[0].squares[i].values = new float[firstDownsampleWidth][firstDownsampleWidth];
        }

        //second filters
        network.filterLayers[1] = new SquareLayer();
        network.filterLayers[1].squares = new Square[secondFilterCount];//amount of filters
        for (int i = 0; i < secondFilterCount; i++)
        {
            network.filterLayers[1].squares[i] = new Square();
            if (isSkippedFilter(i))
            {
                network.filterLayers[1].squares[i].width=0;
                continue;
            }
            network.filterLayers[1].squares[i].width = secondFilterWidth;//width of each square filter in layer
            network.filterLayers[1].squares[i].values = new float[secondFilterWidth][secondFilterWidth];
        }

        //second convolved post filter
        network.convolutedLayers[1] = new SquareLayer();
        network.convolutedLayers[1].squares = new Square[secondConvolutionCount];//Amount of images after convolution
        network.activatedConvolutedLayers[1] = new SquareLayer();
        network.activatedConvolutedLayers[1].squares = new Square[secondConvolutionCount];//Amount of images after convolution
        for (int i = 0; i < secondConvolutionCount; i++)
        {
            network.convolutedLayers[1].squares[i] = new Square();
            network.convolutedLayers[1].squares[i].width = secondConvolutionWidth;//width of images after convolution
            network.convolutedLayers[1].squares[i].values = new float[secondConvolutionWidth][secondConvolutionWidth];
            network.convolutedLayers[1].squares[i].biases = new float[secondConvolutionWidth][secondConvolutionWidth];

            network.activatedConvolutedLayers[1].squares[i] = new Square();
            network.activatedConvolutedLayers[1].squares[i].width = secondConvolutionWidth;//width of images after convolution
            network.activatedConvolutedLayers[1].squares[i].values = new float[secondConvolutionWidth][secondConvolutionWidth];
            network.activatedConvolutedLayers[1].squares[i].biases = new float[secondConvolutionWidth][secondConvolutionWidth];
        }

        //second downsample
        network.downsampledLayers[1] = new SquareLayer();
        network.downsampledLayers[1].squares = new Square[secondConvolutionCount];//amount of downsampled images
        for (int i = 0; i < secondConvolutionCount; i++)
        {
            network.downsampledLayers[1].squares[i] = new Square();
            network.downsampledLayers[1].squares[i].width = secondDownsampleWidth;//width of square downsampled images
            network.downsampledLayers[1].squares[i].values = new float[secondDownsampleWidth][secondDownsampleWidth];
        }

        //first fully connected
        network.hiddenNeurons = new SingleDimension[hiddenLayerCount];
        network.activatedHiddenNeurons = new SingleDimension[hiddenLayerCount];
        network.biases = new SingleDimension[hiddenLayerCount+1];
        network.biases[0] = new SingleDimension();
        network.biases[0].values = new float[firstHiddenLayerNeuronCount];
        network.hiddenNeurons[0] = new SingleDimension();
        network.hiddenNeurons[0].values = new float[firstHiddenLayerNeuronCount];
        network.activatedHiddenNeurons[0] = new SingleDimension();
        network.activatedHiddenNeurons[0].values = new float[firstHiddenLayerNeuronCount];
        network.weights = new SingleDimension[hiddenLayerCount+1];
        network.weights[0] = new SingleDimension();
        network.weights[0].values = new float[firstWeightCount];


        //second fully connected
        network.biases[1] = new SingleDimension();
        network.biases[1].values = new float[secondHiddenLayerNeuronCount];
        network.hiddenNeurons[1] = new SingleDimension();
        network.hiddenNeurons[1].values = new float[secondHiddenLayerNeuronCount];
        network.activatedHiddenNeurons[1] = new SingleDimension();
        network.activatedHiddenNeurons[1].values = new float[secondHiddenLayerNeuronCount];
        network.weights[1] = new SingleDimension();
        network.weights[1].values = new float[secondWeightCount];

        //output layer and weights
        network.biases[2] = new SingleDimension();
        network.biases[2].values = new float[outputNeuronCount];
        network.outputs = new SingleDimension();
        network.outputs.values = new float[outputNeuronCount];
        network.activatedOutputs = new SingleDimension();
        network.activatedOutputs.values = new float[outputNeuronCount];
        network.weights[2] = new SingleDimension();
        network.weights[2].values = new float[thirdWeightCount];
    }

    /**
     * Checks filter position with filter distribution to see if it is a filter that is skipped so that it's width can be set to 0.
     * @param pos One dimensional filter position.
     * @return Whether or not the filter at that position is one which is skipped.
     */
    static public boolean isSkippedFilter(int pos) {
        switch (pos) {
            case 7:
                return true;
            case 10:
                return true;
            case 14:
                return true;
            case 17:
                return true;
            case 18:
                return true;
            case 21:
                return true;
            case 25:
                return true;
            case 26:
                return true;
            case 32:
                return true;
            case 33:
                return true;
            case 39:
                return true;
            case 40:
                return true;
            case 46:
                return true;
            case 47:
                return true;
            case 48:
                return true;
            case 53:
                return true;
            case 54:
                return true;
            case 55:
                return true;
            case 61:
                return true;
            case 62:
                return true;
            case 63:
                return true;
            case 68:
                return true;
            case 69:
                return true;
            case 70:
                return true;
            case 75:
                return true;
            case 76:
                return true;
            case 77:
                return true;
            case 78:
                return true;
            case 82:
                return true;
            case 83:
                return true;
            case 84:
                return true;
            case 85:
                return true;
            case 89:
                return true;
            case 90:
                return true;
            case 91:
                return true;
            case 92:
                return true;
        }
        return false;
    }

    public static void resetNetworkNeurons(ConvolutionalNeuralNetwork network)
    {
        //convolved post filter
        for (int i = 0; i < firstFilterLayerCount; i++)
        {
            network.convolutedLayers[0].squares[i].values = new float[firstConvolutionWidth][firstConvolutionWidth];

            network.activatedConvolutedLayers[0].squares[i].values = new float[firstConvolutionWidth][firstConvolutionWidth];
        }

        //first downsample
        for (int i = 0; i < firstFilterLayerCount; i++)
        {
            network.downsampledLayers[0].squares[i].values = new float[firstDownsampleWidth][firstDownsampleWidth];
        }

        //second convolved post filter
        for (int i = 0; i < secondConvolutionCount; i++)
        {
            network.convolutedLayers[1].squares[i].values = new float[secondConvolutionWidth][secondConvolutionWidth];

            network.activatedConvolutedLayers[1].squares[i].values = new float[secondConvolutionWidth][secondConvolutionWidth];
        }

        //second downsample
        for (int i = 0; i < secondConvolutionCount; i++)
        {
            network.downsampledLayers[1].squares[i].values = new float[secondDownsampleWidth][secondDownsampleWidth];
        }

        //first fully connected
        network.hiddenNeurons = new SingleDimension[hiddenLayerCount];
        network.activatedHiddenNeurons = new SingleDimension[hiddenLayerCount];
        network.hiddenNeurons[0] = new SingleDimension();
        network.hiddenNeurons[0].values = new float[firstHiddenLayerNeuronCount];
        network.activatedHiddenNeurons[0] = new SingleDimension();
        network.activatedHiddenNeurons[0].values = new float[firstHiddenLayerNeuronCount];


        //second fully connected
        network.hiddenNeurons[1] = new SingleDimension();
        network.hiddenNeurons[1].values = new float[secondHiddenLayerNeuronCount];
        network.activatedHiddenNeurons[1] = new SingleDimension();
        network.activatedHiddenNeurons[1].values = new float[secondHiddenLayerNeuronCount];

        //output layer and weights
        network.outputs.values = new float[outputNeuronCount];
        network.activatedOutputs.values = new float[outputNeuronCount];
    }
}
