package com.github.bruce.thrift.connection.counter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class EffetiveCycleCounter extends CycleCounter {
    private final static long PARK_TIME = 500L * 1000;

    private AtomicInteger counter;
    public EffetiveCycleCounter(int size) {
        super(size);
        counter = new AtomicInteger(0);
    }
    @Override
    public int next() {
        for (;;) {
            int value = counter.get();
            int next = (value + 1) % size;
            if (counter.compareAndSet(value, next)) {
                return value;
            }
            else {
                LockSupport.parkNanos(PARK_TIME);
            }
        }
    }
}
