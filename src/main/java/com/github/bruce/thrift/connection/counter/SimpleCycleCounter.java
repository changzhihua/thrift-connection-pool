package com.github.bruce.thrift.connection.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleCycleCounter extends CycleCounter {
    private AtomicInteger counter;
    public SimpleCycleCounter(int size) {
        super(size);
        counter = new AtomicInteger(0);
    }
    @Override
    public int next() {
        int value = Math.abs(counter.getAndIncrement()) % size;
        return value;
    }
}
