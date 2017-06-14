package com.jcstudio.hearthstoneai.learning;

import android.util.Log;

import java.util.Random;

/**
 * Created by jeffrey on 2017/6/9.
 */

public class GeneticAlgorithm implements EvolutionAlgorithmRunner.Evolver {
    private double mutationDev;

    public GeneticAlgorithm(double mutationDev){
        this.mutationDev = mutationDev;
    }

    @Override
    public void evolve(
            EvolutionAlgorithmDatabase db,
            double[][] population,
            double[] fitness,
            int generation,
            EvolutionAlgorithmRunner.EvolveCallback callback) {

        int populationSize = population.length;

        if(callback != null){
            callback.onProgressUpdate("基因雜交", 0, populationSize/2);
        }

        double totalFitness = 0;
        for(double f : fitness){
            totalFitness += f;
        }

        int[] parents = new int[2];

        for(int i = 0; i < populationSize/2; i++){
            for(int j = 0; j < 2; j++){
                double r = Math.random() * totalFitness;
                parents[j] = populationSize-1;
                for(int k = 0; k < populationSize; k++){
                    r -= fitness[k];
                    if(r < 0){
                        parents[j] = k;
                        break;
                    }
                }
            }

            double[][] genes = new double[2][];
            for(int j = 0; j < 2; j++) {
                genes[j] = population[parents[j]];
            }
            crossover(genes);
            for(double[] gene:genes){
                mutation(gene);
            }

            db.add(genes[0], generation+1, i*2);
            db.add(genes[1], generation+1, i*2+1);

            if(callback != null){
                callback.onProgressUpdate("基因雜交", i+1, populationSize/2);
            }
        }
        if(callback != null){
            callback.onProgressUpdate("移除舊世代", 0, 1);
        }
        db.deleteGeneration(generation);
        if(callback != null){
            callback.onProgressUpdate("完成", 0, 0);
        }
    }

    private void crossover(double[][] genes){
        Random r = new Random();
        int randomPoint = r.nextInt(genes[0].length);
        for(int i = randomPoint; i < genes[0].length; i++){
            double temp = genes[0][i];
            genes[0][i] = genes[1][i];
            genes[1][i] = temp;
        }
    }

    private void mutation(double[] gene){
        for(int i = 0; i < gene.length; i++){
            double sum = 0;
            for(int j = 0; j < 3; j++){
                sum += Math.random();
            }
            double r = (sum*2-3)*mutationDev;
            gene[i] += r;
        }
    }
}
