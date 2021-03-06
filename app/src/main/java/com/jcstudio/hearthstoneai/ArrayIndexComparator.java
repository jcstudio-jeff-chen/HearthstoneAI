package com.jcstudio.hearthstoneai;

import java.util.Comparator;

/**
 * Created by jeffrey on 2017/6/8.
 */

public class ArrayIndexComparator<T extends Comparable<T>> implements Comparator<Integer> {
    private final T[] array;

    public ArrayIndexComparator(T[] array) {
        this.array = array;
    }

    public Integer[] createIndexArray() {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
        // Autounbox from Integer to int to use as array indexes
        return -array[index1].compareTo(array[index2]);
    }
}