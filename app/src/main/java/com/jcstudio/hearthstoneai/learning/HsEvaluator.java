package com.jcstudio.hearthstoneai.learning;

import android.util.Log;

import com.jcstudio.hearthstoneai.AI;
import com.jcstudio.hearthstoneai.GameRunner;

/**
 * Created by jeffrey on 2017/6/9.
 */

public class HsEvaluator implements EvolutionAlgorithmRunner.Evaluator {
    public double fightRate = 1;
    @Override
    public double[] fitness(double[][] population, EvolutionAlgorithmRunner.EvolveCallback callback) {
        int populationSize = population.length;
        final int[] wins = new int[populationSize];
        final int[] fights = new int[populationSize];
        int total = populationSize*(populationSize-1)/2;
        if(callback != null){
            callback.onProgressUpdate("戰鬥模擬", 0, total);
        }
        int counter = 0;
        for(int i = 0; i < populationSize; i++){
            AI aiA = new AI(population[i]);
            for(int j = i+1; j < populationSize; j++){
                if(Math.random() < fightRate) {
                    AI aiB = new AI(population[j]);
                    GameRunner runner = new GameRunner(aiA, aiB, null);
                    long startTime = System.nanoTime();
                    int winner = runner.start(null);
                    long endTime = System.nanoTime();
                    Log.d("HsEvaluator", "模擬戰鬥一場需時 " + ((endTime - startTime) / 1000000000.0) + "秒");
                    if (winner == 0) {
                        wins[i]++;
                    } else {
                        wins[j]++;
                    }
                    fights[i]++;
                    fights[j]++;
                }
                counter++;
                if(callback != null){
                    callback.onProgressUpdate("戰鬥模擬", counter, total);
                }
            }
        }
        double[] output = new double[populationSize];
        for(int i = 0; i < populationSize; i++){
            if(fights[i] == 0){
                output[i] = 0.5;
            } else {
                output[i] = (double)wins[i]/(fights[i]);
            }

        }
        return output;
    }
}
