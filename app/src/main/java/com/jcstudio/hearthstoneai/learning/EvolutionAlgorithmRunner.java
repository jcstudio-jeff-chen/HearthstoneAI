package com.jcstudio.hearthstoneai.learning;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.speech.tts.Voice;
import android.util.Log;

import com.jcstudio.hearthstoneai.LocalStorage;

/**
 * Created by jeffrey on 2017/6/9.
 */

public class EvolutionAlgorithmRunner {
    public static final String GENERATION = "generation";
    public static final String INITIALIZED = "initialized";
    public static final String MAX_FITNESS = "max_fitness";
    private EvolutionAlgorithmDatabase db;
    private LocalStorage sp;
    private int populationSize;
    public int dimension;
    private Evolver evolver;
    private Evaluator evaluator;
    public int generation;

    public EvolutionAlgorithmRunner(Context context, int populationSize, int dimension, Evolver evolver, Evaluator evaluator){
        this.populationSize = populationSize;
        this.dimension = dimension;
        this.evolver = evolver;
        this.evaluator = evaluator;
        db = new EvolutionAlgorithmDatabase(context, dimension);
        sp = new LocalStorage(context);
        generation = sp.getInt(GENERATION);
    }

    public void initialize(final double[] lowerBound, final double[] upperBound, final InitCallback callback){
        db.deleteAll();
        sp.clear();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(callback != null){
                    callback.onStart(populationSize);
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if(callback != null){
                    callback.onProgressUpdate(values[0], populationSize);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                for(int i = 0; i < populationSize; i++){
                    double[] vector = new double[dimension];
                    for(int j = 0; j < dimension; j++){
                        double lb = lowerBound == null ? -1 : lowerBound[j];
                        double ub = upperBound == null ? 1 : upperBound[j];
                        vector[j] = Math.random() * (ub - lb) + lb;
                    }
                    db.add(vector, generation, i);
                    publishProgress(i+1);
                }
                generation = 0;
                sp.saveInt(GENERATION, generation);
                sp.saveBoolean(INITIALIZED, true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(callback != null){
                    callback.onInitFinish(populationSize);
                }
            }
        }.execute();
    }

    public boolean initIfNotExist(double[] lowerBound, double[] upperBound, InitCallback callback){
        boolean initialized = sp.getBoolean(INITIALIZED, false);
        if(!initialized){
            initialize(lowerBound, upperBound, callback);
        }
        return initialized;
    }

    public void evolve(EvolveCallback callback){
        double[][] population = new double[populationSize][];
        if(callback != null){
            callback.onProgressUpdate("載入參數", 0, populationSize);
        }
        for(int i = 0; i < populationSize; i++){
            population[i] = db.get(i);
            if(callback != null){
                callback.onProgressUpdate("載入參數", i+1, populationSize);
            }
        }
        double[] fitness = evaluator.fitness(population, callback);
        double maxFitness = -1;
        for(int i = 0; i < fitness.length; i++){
            if(maxFitness < fitness[i]){
                maxFitness = fitness[i];
            }
        }
        sp.saveFloat(MAX_FITNESS, (float) maxFitness);
        evolver.evolve(db, population, fitness, generation, callback);
        generation++;
        sp.saveInt(GENERATION, generation);
    }

    public void evolveAsync(final EvolveCallback callback){
        new AsyncTask<Void, Void, Void>(){
            private long startTime;
            @Override
            protected Void doInBackground(Void... params) {
                startTime = System.nanoTime();
                evolve(callback);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(callback != null){
                    long endTime = System.nanoTime();
                    callback.onEvolved(generation, (endTime-startTime)/1000000000.0, db);
                }
            }
        }.execute();
    }

    public interface Evolver{
        void evolve(EvolutionAlgorithmDatabase db, double[][] population, double[] fitness, int generation, EvolveCallback callback);
    }

    public interface Evaluator{
        double[] fitness(double[][] population, EvolveCallback callback);
    }

    public interface EvolveCallback{
        void onProgressUpdate(String job, int progress, int total);
        void onEvolved(int generation, double time, EvolutionAlgorithmDatabase db);
    }

    public interface InitCallback{
        void onStart(int total);
        void onProgressUpdate(int progress, int total);
        void onInitFinish(int total);
    }
}
