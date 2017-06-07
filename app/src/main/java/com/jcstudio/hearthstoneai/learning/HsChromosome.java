package com.jcstudio.hearthstoneai.learning;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.apache.commons.math3.util.DoubleArray;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jeffrey on 2017/6/7.
 */

public abstract class HsChromosome extends AbstractListChromosome<Double> {
    public HsChromosome(List<Double> representation) throws InvalidRepresentationException {
        super(representation);
    }

    public HsChromosome(double[] representation) throws InvalidRepresentationException {
        super();
    }

    public HsChromosome(List<Double> representation, boolean copyList) {
        super(representation, copyList);
    }

    @Override
    protected void checkValidity(List<Double> chromosomeRepresentation) throws InvalidRepresentationException {

    }

    @Override
    public AbstractListChromosome<Double> newFixedLengthChromosome(List<Double> chromosomeRepresentation) {
        return null;
    }
}
