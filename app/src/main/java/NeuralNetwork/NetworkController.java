package NeuralNetwork;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.NetworkOnMainThreadException;
import android.security.NetworkSecurityPolicy;
import android.util.Log;

import java.io.IOException;

public class NetworkController {
    public int temperature;
    ConvolutionalNeuralNetwork network;
    Bitmap bitmap;

    public NetworkController(Context context){
        String netPath = "savedNet";

        try {
            network = NetworkFileLoader.loadNet(netPath, context.getAssets());
        } catch (IOException e) {
            Log.e("File", "Error loading neural net file.");
            e.printStackTrace();
            network = new ConvolutionalNeuralNetwork();
        }


    }

    public float getOutput(int pos){
        if(pos>network.outputs.values.length)
            return 0.0f;
        return network.outputs.values[pos];
    }

    public float getErrorEstimate(){
        float errEstimate=0.0f;
        for(int i=0; i<10; i++){
            if(i==network.numberGuess) {
                errEstimate -= network.outputs.values[network.numberGuess];
                continue;
            }
            errEstimate+=network.outputs.values[i];
        }
        return errEstimate;
    }

    public int getNumber(){
        NetworkInitializer.resetNetworkNeurons(network);
        NetworkInitializer.resetOutputs(network);
        NetworkPropagator.forwardPropagate(network);
        return network.numberGuess;
    }


    public void setBitmap(Bitmap bitmap){
        NetworkInitializer.resetNetworkNeurons(network);
        this.bitmap = bitmap;
        NetworkInitializer.setInputs(network, bitmap);
    }
}
