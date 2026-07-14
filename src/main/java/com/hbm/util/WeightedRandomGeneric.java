package com.hbm.util;

public class WeightedRandomGeneric<T> {
    private final T value;
    private final int weight;

    public WeightedRandomGeneric(T value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public T get() {
        return value;
    }

    public int getWeight() {
        return weight;
    }
}
