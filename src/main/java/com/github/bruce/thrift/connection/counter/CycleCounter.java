package com.github.bruce.thrift.connection.counter;

/**
 * Created by didi on 18/6/10.
 */
public abstract class CycleCounter {
    protected int size;

    public CycleCounter(int size) {
        this.size = size;
    }

    public abstract int next();
}
