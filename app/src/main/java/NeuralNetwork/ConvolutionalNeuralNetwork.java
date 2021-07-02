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

    class SingleDimension {
        public float[] values;
    };

    static class Square{
        public int width;
        public float[][] values;
        public float[][] biases;
        public Square(){}
        public void applyBiases() {
            for (int y = 0; y < values[0].length; y++) {
                for (int x = 0; x < values.length; x++){
                    values[x][y] += biases[x][y];
                }
            }
        }
    };

    static class SquareLayer{
        public Square[] squares;
        public SquareLayer(){}
    }

    public ConvolutionalNeuralNetwork(){}



}
