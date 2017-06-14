package com.jcstudio.hearthstoneai;

import android.util.Log;

import com.jcstudio.hearthstoneai.learning.HsNeuralNetwork;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Created by jeffrey on 2017/6/2.
 */

public class AI extends HsNeuralNetwork{

    public AI(){
        super();
    }

    public AI(double[] params){
        super();
        super.applyParams(params);
    }

    public ArrayList<Integer> getActionList(double[] dataArray){
        double[] output = calculate(dataArray);
        ArrayIndexComparator<Double> comparator = new ArrayIndexComparator<>(ArrayUtils.toObject(output));
        Integer[] choice = comparator.createIndexArray();
        Arrays.sort(choice, comparator);

        return new ArrayList<>(Arrays.asList(choice));
    }
}
