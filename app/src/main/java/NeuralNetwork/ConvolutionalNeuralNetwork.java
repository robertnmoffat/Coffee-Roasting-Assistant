package NeuralNetwork;

public class ConvolutionalNeuralNetwork {
    public Square input;

    public SquareLayer[] filterLayers;
    public SquareLayer[] convolutedLayers;
    public SquareLayer[] activatedConvolutedLayers;
    public SquareLayer[] downsampledLayers;

    public SingleDimension[] weights;
    public SingleDimension[] biases;
    public SingleDimension[] hiddenNeurons;
    public SingleDimension[] activatedHiddenNeurons;

    public SingleDimension outputs;
    public SingleDimension activatedOutputs;

    public int numberGuess = -1;

    public ConvolutionalNeuralNetwork(){}



}
