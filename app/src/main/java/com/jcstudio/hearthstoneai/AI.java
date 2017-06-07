package com.jcstudio.hearthstoneai;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by jeffrey on 2017/6/2.
 */

public class AI {
    public NeuralNetwork neuralNetwork;

    public static int nHiddenLayerNodes(){
        return (BoardData.arraySize() + Game.POSSIBLE_ACTIONS)/2;
    }

    public AI(){
        neuralNetwork = new NeuralNetwork();
        neuralNetwork.addLayer(new Layer(BoardData.arraySize(), nHiddenLayerNodes()));
        neuralNetwork.addLayer(new Layer(nHiddenLayerNodes(), Game.POSSIBLE_ACTIONS));
    }

    public AI(double[] param){
        this();
        neuralNetwork.applyParams(param);
    }

    public ArrayList<Integer> getActionList(double[] dataArray){
        ArrayList<Integer> actions = new ArrayList<>(67);
        for(int i = 0; i < 67; i++){
            actions.add(i);
        }
        Collections.shuffle(actions);
        return actions;
    }
}
