package com.github.bruce.thrift.connection.factory;

import java.io.Serializable;

public class HostAndPort implements Serializable {
    private static final long serialVersionUID = 1;
    //public static final String LOCALHOST_STR = getLocalHostQuietly();


    private String host;
    private int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HostAndPort)) {
            return false;
        }
        HostAndPort other = (HostAndPort)obj;
        return port == other.port && host.equals(other.host);
    }

    @Override
    public int hashCode() {
        return 31 * host.hashCode() + port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
