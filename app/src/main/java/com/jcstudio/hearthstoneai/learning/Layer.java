package com.jcstudio.hearthstoneai.learning;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by jeffrey on 2017/6/6.
 */

public class Layer {
    private RealMatrix weight;
    private RealVector bias;

    public Layer(int nInput, int nOutput){
        weight = MatrixUtils.createRealMatrix(nOutput, nInput);
        bias = MatrixUtils.createRealVector(new double[nOutput]);
    }

    public int nInput(){
        return weight.getColumnDimension();
    }

    public int nOutput(){
        return weight.getRowDimension();
    }

    public int nParam(){
        if(nOutput() == 0){
            return 0;
        }
        return (nInput() + 1) * nOutput();
    }

    public void applyParam(double[] param, int start){
        int index = start;
        for(int i = 0; i < nOutput(); i++){
            for(int j = 0; j < nInput(); j++){
                weight.setEntry(i, j, param[index]);
                index++;
            }
        }
        for(int i = 0; i < nOutput(); i++){
            bias.setEntry(i, param[index]);
            index++;
        }
    }

    public double[] calculate(double[] input){
        RealVector x = MatrixUtils.createRealVector(input);
        return weight.operate(x).add(bias).toArray();
    }
}
