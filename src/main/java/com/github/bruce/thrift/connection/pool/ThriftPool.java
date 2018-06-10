package com.github.bruce.thrift.connection.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ThriftPool<T> implements Pool<T> {
    private GenericObjectPool<T> internalPool;

    public ThriftPool(BasePooledObjectFactory<T> factory, int maxTotal, int maxWaitMilliseconds) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMilliseconds);
        config.setTestOnBorrow(true);
        initPool(factory, config);
    }

    @Override
    public boolean initPool(BasePooledObjectFactory<T> factory, GenericObjectPoolConfig config) {
        if (internalPool != null) {
            internalPool.close();
        }
        internalPool = new GenericObjectPool<>(factory, config);
        return true;
    }

    @Override
    public T getResource() throws Exception {
        return internalPool.borrowObject();
    }

    @Override
    public void returnResourceObject(T resource, boolean broken) {
        if (broken) {
            try {
                internalPool.invalidateObject(resource);
            } catch (Exception e) {
                // ignore
            }
        }
        else {
            internalPool.returnObject(resource);
        }
    }

    @Override
    public void close() {
        internalPool.close();
    }

}
