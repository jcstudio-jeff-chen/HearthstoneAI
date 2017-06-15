package com.jcstudio.hearthstoneai.learning;

/**
 * Created by jeffrey on 2017/6/15.
 *
 * 第 0 版類神經網路，舊版類神經網路可在不破壞舊參數的情況下擴充為新版。
 */

public class HsNeuralNetworkV0 extends NeuralNetwork {
    public HsNeuralNetworkV0(){
        super();
        addLayer(new Layer(129, 92));
        addLayer(new Layer(92, 67));
    }
}
