package NeuralNetwork;

import android.content.res.AssetManager;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.xpath.XPath;

import androidx.annotation.RequiresApi;

public class NetworkFileLoader {

    /**
     * Converts four bytes from the file input to a single float.
     * @param bytes Bytes from the saved neural network file.
     * @param startPos Position from which to start the count of four bytes.
     * @return
     */
    public static float convertBytesToFloat(byte[] bytes, int startPos) {
        byte[] fBytes = new byte[]{bytes[startPos+0], bytes[startPos+1], bytes[startPos+2], bytes[startPos+3]};
        return ByteBuffer.wrap(fBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public static float convertBytesToFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        int count = is.read(bytes);

        float num = (((bytes[0])<<24)|((bytes[1]&0xFF)<<16)|((bytes[2]&0xFF)<<8)|(bytes[3]));
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        //float bitsfloat = Float.intBitsToFloat(((bytes[3])<<24)|((bytes[2]&0xFF)<<16)|((bytes[1]&0xFF)<<8)|(bytes[0]));
        ////return bitsfloat;
        //return num;

    }

    /**
     * Loads neural network from byte file and converts to ConvolutionalNeuralNetwork object.
     * @param pathString
     * @return
     * @throws IOException
     */
    public static ConvolutionalNeuralNetwork loadNet(String pathString, AssetManager assets) throws IOException {
        ConvolutionalNeuralNetwork network = new ConvolutionalNeuralNetwork();
        NetworkInitializer.InitializeNetwork(network);


        //Path path = Paths.get(pathString);
        //byte[] bytes = Files.readAllBytes(path);
        InputStream is = assets.open(pathString, AssetManager.ACCESS_BUFFER);

        byte[] bytes = new byte[281177];
        int count = is.read(bytes);
        int byteOffset = 0;

        int version = (char)bytes[byteOffset++];
//byte[] bytes = new byte[4];
//int byteOffset = 0;

        for (int l = 0; l < network.filterLayers[0].squares.length; l++)
        {
            for (int y = 0; y < network.filterLayers[0].squares[0].width; y++)
            {
                for (int x = 0; x < network.filterLayers[0].squares[0].width; x++)
                {
                    network.filterLayers[0].squares[l].values[x][y] = convertBytesToFloat(bytes, byteOffset);
                    byteOffset+=4;
                }
            }
        }
        for (int l = 0; l < network.filterLayers[1].squares.length; l++)
        {
            for (int y = 0; y < network.filterLayers[1].squares[0].width; y++)
            {
                for (int x = 0; x < network.filterLayers[1].squares[0].width; x++)
                {
                    if (network.filterLayers[1].squares[l].values != null)
                        network.filterLayers[1].squares[l].values[x][y] = convertBytesToFloat(bytes, byteOffset);
                    byteOffset+=4;
                }
            }
        }

        //----------------convoluted layers biases-------------
        for (int l = 0; l < network.convolutedLayers[0].squares.length; l++)
        {
            for (int y = 0; y < network.convolutedLayers[0].squares[0].width; y++)
            {
                for (int x = 0; x < network.convolutedLayers[0].squares[0].width; x++)
                {
                    network.convolutedLayers[0].squares[l].biases[x][y] = convertBytesToFloat(bytes, byteOffset);
                    byteOffset+=4;
                }
            }
        }
        for (int l = 0; l < network.convolutedLayers[1].squares.length; l++)
        {
            for (int y = 0; y < network.convolutedLayers[1].squares[0].width; y++)
            {
                for (int x = 0; x < network.convolutedLayers[1].squares[0].width; x++)
                {
                    network.convolutedLayers[1].squares[l].biases[x][y] = convertBytesToFloat(bytes, byteOffset);
                    byteOffset+=4;
                }
            }
        }

        //---------------weight layer values----------------
        for (int i = 0; i < network.weights[0].values.length; i++)
        {
            network.weights[0].values[i] = convertBytesToFloat(bytes, byteOffset);
            byteOffset+=4;
        }
        for (int i = 0; i < network.weights[1].values.length; i++)
        {
            network.weights[1].values[i] = convertBytesToFloat(bytes, byteOffset);
            byteOffset+=4;
        }
        for (int i = 0; i < network.weights[2].values.length; i++)
        {
            network.weights[2].values[i] = convertBytesToFloat(bytes, byteOffset);
            byteOffset+=4;
        }

        //-------------bias layer values------------------
        for (int i = 0; i < network.biases[0].values.length; i++)
        {
            network.biases[0].values[i] = convertBytesToFloat(bytes, byteOffset);
            byteOffset+=4;
        }
        for (int i = 0; i < network.biases[1].values.length; i++)
        {
            network.biases[1].values[i] = convertBytesToFloat(bytes, byteOffset);
            byteOffset+=4;
        }

        return network;
    }
}
