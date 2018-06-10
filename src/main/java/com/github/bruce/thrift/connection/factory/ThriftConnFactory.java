package com.github.bruce.thrift.connection.factory;

import java.util.ArrayList;
import java.util.List;

import com.github.bruce.thrift.connection.counter.CycleCounter;
import com.github.bruce.thrift.connection.counter.EffetiveCycleCounter;
import com.github.bruce.thrift.connection.utils.Preconditions;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;

public abstract class ThriftConnFactory<T extends TServiceClient> extends BasePooledObjectFactory<T> {

    private CycleCounter counter;
    public List<HostAndPort> hostAndPorts;

    public ThriftConnFactory(List<String> hosts, int port) {
        Preconditions.checkArgument(hosts != null && hosts.size() > 0, "The hosts can not be empty");
        Preconditions.checkArgument(port > 0 && port < 65535, "The port is not valid for its value out of range of 0 and 65535");
        hostAndPorts = new ArrayList<>(hosts.size());
        for (String host : hosts) {
            hostAndPorts.add(new HostAndPort(host, port));
        }
        counter = new EffetiveCycleCounter(hostAndPorts.size());
    }

    public ThriftConnFactory(List<HostAndPort> hostAndPorts) {
        Preconditions.checkArgument(hostAndPorts != null && hostAndPorts.size() > 0, "The hosts can not be empty");
        this.hostAndPorts = hostAndPorts;

    }

    @Override
    public abstract T create() throws Exception;

    @Override
    public PooledObject<T> wrap(T obj) {
        return new DefaultPooledObject<T>(obj);
    }

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception  {
        TTransport transport = getTTransportFromClient(p);
        if (transport.isOpen()) {
            transport.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<T> p) {
        TTransport transport = getTTransportFromClient(p);
        return transport.isOpen();
    }

    @Override
    public void activateObject(PooledObject<T> p) throws Exception {
        TTransport transport = getTTransportFromClient(p);
        if (!transport.isOpen()) { // 如果链接关闭,重新打开
            transport.close();
            transport.open();
        }
    }

    private TTransport getTTransportFromClient(PooledObject<T> p) {

        return p.getObject().getInputProtocol().getTransport();
    }

    protected HostAndPort getHost() {
        int index = counter.next();
        return hostAndPorts.get(index);
    }

}

