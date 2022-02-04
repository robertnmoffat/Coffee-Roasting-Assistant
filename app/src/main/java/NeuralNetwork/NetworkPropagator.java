package NeuralNetwork;


public class NetworkPropagator {


    static public void forwardPropagate(ConvolutionalNeuralNetwork net) {
        //----------first convolution layer
        for (int i = 0; i<net.filterLayers[0].squares.length; i++)
        {
            applyFilter(net.input, net.filterLayers[0].squares[i], net.convolutedLayers[0].squares[i]);
            net.convolutedLayers[0].squares[i].applyBiases();
            reluSquare(net.convolutedLayers[0].squares[i], net.activatedConvolutedLayers[0].squares[i]);
            maxPool(net.activatedConvolutedLayers[0].squares[i], net.downsampledLayers[0].squares[i]);
        }

        //--------------second convolution
        int filterPos = 0;
        for (int c = 0; c < net.convolutedLayers[1].squares.length; c++)
        {
            for (int f = 0; f < 6; f++)
            {
                applyFilterAdditively(net.downsampledLayers[0].squares[filterPos % 6], net.filterLayers[1].squares[filterPos], net.convolutedLayers[1].squares[c]);
                filterPos++;
            }
            net.convolutedLayers[1].squares[c].applyBiases();
            reluSquare(net.convolutedLayers[1].squares[c], net.activatedConvolutedLayers[1].squares[c]);//apply relu to square
            maxPool(net.activatedConvolutedLayers[1].squares[c], net.downsampledLayers[1].squares[c]);
        }

        //-------------fully connected
        int neuronCount = net.hiddenNeurons[0].values.length;
        int filterCount = net.downsampledLayers[1].squares.length;

        for (int i=0; i<neuronCount; i++) {
            net.hiddenNeurons[0].values[i]=0.0f;//reset for this iteration
            for (int j = 0; j < filterCount; j++) {
                int filterHeight = net.downsampledLayers[1].squares[0].width;
                int filterWidth = net.downsampledLayers[1].squares[0].width;

                //iterate through square
                for (int y = 0; y < filterHeight; y++) {
                    for (int x = 0; x < filterWidth; x++)
                    {
                        //weightPos iterates through each filter as you would read a book. horizontal-vertical-page-book (page being each filter, book being set of filters per neuron)
                        net.hiddenNeurons[0].values[i] += net.weights[0].values[i*filterCount*filterHeight * filterWidth + j*filterHeight*filterWidth + y* filterWidth + x] * net.downsampledLayers[1].squares[j].values[x][y];
                    }
                }
            }
            net.hiddenNeurons[0].values[i] += net.biases[0].values[i];//Add bias to neuron after summing weighted inputs
            net.activatedHiddenNeurons[0].values[i] = (float)sigmoid(net.hiddenNeurons[0].values[i]);//after adding all values for current neuron, run it through sigmoid
        }

        //fully connected layer 2
        for (int i = 0; i < net.hiddenNeurons[1].values.length; i++)
        {
            net.hiddenNeurons[1].values[i] = 0.0f;//reset
            for (int j = 0; j < net.hiddenNeurons[0].values.length; j++)
            {
                net.hiddenNeurons[1].values[i] += net.weights[1].values[j * net.hiddenNeurons[1].values.length + i] * net.activatedHiddenNeurons[0].values[j];
            }
            net.hiddenNeurons[1].values[i] += net.biases[1].values[i];//Add bias to neuron after summing weighted inputs
            net.activatedHiddenNeurons[1].values[i] = (float)sigmoid(net.hiddenNeurons[1].values[i]);//after adding all values for current neuron, run it through sigmoid
        }

        //fully connected output
        for (int i = 0; i < net.outputs.values.length; i++) {
            net.outputs.values[i] = 0.0f;//reset
            for (int j = 0; j < net.hiddenNeurons[1].values.length; j++) {
                net.outputs.values[i] += net.weights[2].values[j* net.outputs.values.length+i] * net.activatedHiddenNeurons[1].values[j];
            }
            net.outputs.values[i] += net.biases[2].values[i];
            //No bias in output layer
            net.activatedOutputs.values[i] = (float)sigmoid(net.outputs.values[i]);//Copy over outputs so that softmax can be applied
        }
        //softMax(ref net.activatedOutputs);

        int highestPos = -1;
        float highestVal = Float.MIN_VALUE;
        for (int i=0; i<net.activatedOutputs.values.length; i++) {
            if (net.activatedOutputs.values[i] > highestVal) {
                highestVal = net.activatedOutputs.values[i];
                highestPos = i;
            }
        }
        net.numberGuess = highestPos;
    }


    static public void applyFilter(Square input, Square filter, Square convolution)
    {
        float positionSum = 0.0f;
        for (int posy = 0; posy < convolution.width; posy++)
        {
            for (int posx = 0; posx < convolution.width; posx++)
            {
                positionSum = 0.0f;

                for (int y = 0; y < filter.width; y++)
                {
                    for (int x = 0; x < filter.width; x++)
                    {
                        float inp = input.values[posx + x][posy + y];
                        float filt = filter.values[x][y];
                        positionSum += inp * filt;
                    }
                }
                convolution.values[posx][posy] = positionSum;
            }
        }
    }

    static public void applyFilterAdditively(Square input, Square filter, Square convolution)
    {
        float positionSum = 0.0f;
        for (int posy = 0; posy < convolution.width; posy++)
        {
            for (int posx = 0; posx < convolution.width; posx++)
            {
                positionSum = 0.0f;

                for (int y = 0; y < filter.width; y++)
                {
                    for (int x = 0; x < filter.width; x++)
                    {
                        positionSum += input.values[posx + x][posy + y] * filter.values[x][y];
                    }
                }
                convolution.values[posx][posy] += positionSum;
            }
        }
    }

    static public double sigmoid(float num) {
        double epow = Math.pow(Math.E, -num);
        double log = Math.log(Double.MAX_VALUE);
        if (num > Math.log(Double.MAX_VALUE)) {
            //Console.WriteLine("Nan zone");
        }
        double output = 1 / (1 + Math.exp(-num));
        if(output==0)
            output = 1 / (1 + Math.exp(709));
        return output;
    }

    static public void maxPool(Square inputLayer, Square downsampled) {
        for (int posy = 0; posy < downsampled.width; posy++)
        {
            for (int posx = 0; posx < downsampled.width; posx++)
            {
                float highest = Float.MIN_VALUE;
                for (int y = 0; y < 2; y++)
                {
                    for (int x = 0; x < 2; x++)
                    {
                        if (inputLayer.values[posx*2 + x][posy*2 + y] > highest)
                        highest = inputLayer.values[posx*2 + x][posy*2 + y];
                    }
                }
                downsampled.values[posx][posy] = highest;
            }
        }
    }

    static public void reluSquare(Square input, Square ouput) {
        for (int y = 0; y < input.width; y++) {
            for (int x=0; x<input.width; x++) {
                ouput.values[x][y] = input.values[x][y] >= 0 ? input.values[x][y] : 0.01f* input.values[x][y];//if less than zero set to zero else keep.
            }
        }
    }

    static public void softMax(SingleDimension input) {
        float sum = 0.0f;
        for (int i = 0; i < input.values.length; i++) {
            sum+=(float)Math.pow(Math.E, input.values[i]);
        }
        for (int i = 0; i < input.values.length; i++)
        {
            float pow = (float)Math.pow(Math.E, input.values[i]);
            float value = pow / sum;
            input.values[i] = value;
        }
    }
}
