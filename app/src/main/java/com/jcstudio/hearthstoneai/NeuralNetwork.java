package com.jcstudio.hearthstoneai;

import java.util.ArrayList;

/**
 * Created by jeffrey on 2017/6/6.
 */

public class NeuralNetwork {
    private ArrayList<Layer> layers = new ArrayList<>();

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
        return result;
    }
}
