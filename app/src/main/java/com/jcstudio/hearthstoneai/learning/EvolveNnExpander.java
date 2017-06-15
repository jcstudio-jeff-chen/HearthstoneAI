package com.jcstudio.hearthstoneai.learning;

import android.content.Context;
import android.os.AsyncTask;

import com.jcstudio.hearthstoneai.LocalStorage;

import static com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner.GENERATION;

/**
 * Created by jeffrey on 2017/6/15.
 */

public class EvolveNnExpander {
    private EvolutionAlgorithmDatabase db;
    private LocalStorage sp;
    private NeuralNetwork oldNn;
    private NeuralNetwork newNn;
    private int populationSize;

    public EvolveNnExpander(Context context, NeuralNetwork oldNn,  NeuralNetwork newNn, int populationSize){
        this.oldNn = oldNn;
        this.newNn = newNn;
        this.populationSize = populationSize;
        db = new EvolutionAlgorithmDatabase(context, oldNn.nParam());
        sp = new LocalStorage(context);
    }

    public int nExtraParam(){
        return newNn.nParam() - oldNn.nParam();
    }

    public void expand(double[] lowerBound, double[] upperBound, ProgressListener progressListener){
        int generation = sp.getInt(GENERATION);
        if(progressListener != null){
            progressListener.onProgressUpdate(0, populationSize);
        }
        for(int i = 0; i < populationSize; i++){
            double[] oldParam = db.get(i);
            double[] newParam = new double[newNn.nParam()];
            int oldIndex = 0;
            int newIndex = 0;
            for(int j = 0; j < oldNn.layers.size(); j++){
                Layer oldLayer = oldNn.layers.get(j);
                Layer newLayer = newNn.layers.get(j);
                if(oldLayer.nInput() == newLayer.nInput() && oldLayer.nOutput() == newLayer.nOutput()){
                    continue;
                }
                for(int k = 0; k < oldLayer.nOutput(); k++){
                    for(int l = 0; l < oldLayer.nInput(); l++){
                        newParam[newIndex] = oldParam[oldIndex];
                        newIndex++;
                        oldIndex++;
                    }
                    for(int l = oldLayer.nInput(); l < newLayer.nInput(); l++){
                        double lb = lowerBound == null ? -1 : lowerBound[newIndex];
                        double ub = upperBound == null ? 1 : upperBound[newIndex];
                        double r = Math.random()*(ub-lb) + lb;
                        newParam[newIndex] = r;
                        newIndex++;
                    }
                }
                for(int k = oldLayer.nOutput(); k < newLayer.nOutput(); k++){
                    for(int l = 0; l < newLayer.nInput(); l++){
                        double lb = lowerBound == null ? -1 : lowerBound[newIndex];
                        double ub = upperBound == null ? 1 : upperBound[newIndex];
                        double r = Math.random()*(ub-lb) + lb;
                        newParam[newIndex] = r;
                        newIndex++;
                    }
                }
                for(int k = 0; k < oldLayer.nOutput(); k++){
                    newParam[newIndex] = oldParam[oldIndex];
                    newIndex++;
                    oldIndex++;
                }
                for(int k = oldLayer.nOutput(); k < newLayer.nOutput(); k++){
                    double lb = lowerBound == null ? -1 : lowerBound[newIndex];
                    double ub = upperBound == null ? 1 : upperBound[newIndex];
                    double r = Math.random()*(ub-lb) + lb;
                    newParam[newIndex] = r;
                    newIndex++;
                }
            }
            for(int j = oldNn.layers.size(); j < newNn.layers.size(); j++){
                Layer layer = newNn.layers.get(j);
                for(int k = 0; k < layer.nParam(); k++){
                    double lb = lowerBound == null ? -1 : lowerBound[newIndex];
                    double ub = upperBound == null ? 1 : upperBound[newIndex];
                    double r = Math.random()*(ub-lb) + lb;
                    newParam[newIndex] = r;
                    newIndex++;
                }
            }

            db.delete(i);
            db.add(newParam, generation, i);

            if(progressListener != null){
                progressListener.onProgressUpdate(i+1, populationSize);
            }
        }
    }

    public void expandAsync(double[] lowerBound, double[] upperBound, final ProgressListener listener){
        new AsyncTask<double[], Void, Void>(){
            @Override
            protected Void doInBackground(double[]... params) {
                expand(params[0], params[1], listener);
                return null;
            }
        }.execute(lowerBound, upperBound);
    }

    public interface ProgressListener{
        void onProgressUpdate(int progress, int total);
    }
}
