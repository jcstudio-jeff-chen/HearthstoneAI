package com.jcstudio.hearthstoneai.learning;

import android.content.Context;
import android.content.SharedPreferences;

import com.jcstudio.hearthstoneai.LocalStorage;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.ListPopulation;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.RandomKeyMutation;
import org.apache.commons.math3.genetics.TournamentSelection;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jeffrey on 2017/6/7.
 */

public class Learner {
    public static final int ARITY = 6;
    public static final int POPULATION_SIZE = 50;
    public GeneticAlgorithm ga;
    public LocalStorage db;
    public Learner(Context context){
        db = new LocalStorage(context);
        ga = new GeneticAlgorithm(new OnePointCrossover<Double>(), 1, new RandomKeyMutation(), 0.1, new TournamentSelection(ARITY));
        Population initial = getInitialPopulation();
    }

    public ElitisticListPopulation getInitialPopulation(){
        double[][] population = loadPopulation();
        ArrayList<Chromosome> chromosomes = new ArrayList<>(POPULATION_SIZE);
        ElitisticListPopulation p = new ElitisticListPopulation();
        return p;
    }

    public double[][] loadPopulation(){
        double[][] data = new double[POPULATION_SIZE][];
        for(int i = 0; i < POPULATION_SIZE; i++){
            float[] f = db.getFloatArray("population_" + i);
            data[i] = new double[f.length];
            for(int j = 0; j < f.length; j++){
                data[i][j] = (double) f[j];
            }
        }
        return data;
    }
}
