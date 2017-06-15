package com.jcstudio.hearthstoneai.learning;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jeffrey on 2017/6/6.
 */

public class NeuralNetwork {
    public ArrayList<Layer> layers = new ArrayList<>();

    public void addLayer(Layer layer){
        layers.add(layer);
    }

    public double[] calculate(double[] input){
        double[] output = input;
        for(Layer layer : layers){
            output = layer.calculate(output);
        }
        return output;
    }

    public void applyParams(double[] param){
        int index = 0;
        for(Layer layer : layers){
            layer.applyParam(param, index);
            index += layer.nParam();
        }
    }

    public int nParam(){
        int result = 0;
        for(Layer layer : layers){
            result += layer.nParam();
        }
        return result;
    }

    public void printNodeCount(){
        if(layers.isEmpty()){
            Log.d("NeuralNetwork", "Neural network node counts: 0");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(layers.get(0).nInput());
        for(Layer layer : layers){
            sb.append(" ");
            sb.append(layer.nOutput());
        }
        Log.d("NeuralNetwork", "Neural network node counts: " + sb.toString());
    }
}
