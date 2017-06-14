package com.jcstudio.hearthstoneai.learning;

import com.jcstudio.hearthstoneai.BoardData;
import com.jcstudio.hearthstoneai.Game;

/**
 * Created by jeffrey on 2017/6/8.
 */

public class HsNeuralNetwork extends NeuralNetwork {
    public static int nHiddenLayerNodes(){
        return (int) Math.sqrt(BoardData.arraySize()*Game.POSSIBLE_ACTIONS);
    }
    public HsNeuralNetwork() {
        super();
        addLayer(new Layer(BoardData.arraySize(), nHiddenLayerNodes()));
        addLayer(new Layer(nHiddenLayerNodes(), Game.POSSIBLE_ACTIONS));
    }
}
