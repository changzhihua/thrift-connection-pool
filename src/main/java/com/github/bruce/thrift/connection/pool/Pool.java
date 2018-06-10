package com.github.bruce.thrift.connection.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public interface Pool<T> {

    public boolean initPool(BasePooledObjectFactory<T> factory, GenericObjectPoolConfig config);

    public T getResource() throws Exception;

    public void returnResourceObject(final T resource, boolean broken);

    public void close();

}