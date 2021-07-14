package NeuralNetwork;

public class Square {
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
}
