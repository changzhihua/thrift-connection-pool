package com.github.bruce.thrift;

import com.github.bruce.thrift.connection.factory.HostAndPort;
import com.github.bruce.thrift.connection.factory.ThriftConnFactory;
import com.github.bruce.thrift.connection.pool.Pool;
import com.github.bruce.thrift.connection.pool.ThriftPool;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Collections;
import java.util.List;

public class Example {
	private Pool<ThriftExampleService.Client> pool;
	public Example() {
		pool = createPool();
	}

	private Pool<ThriftExampleService.Client> createPool() {
		//some parameters for test
		List<String> hosts = Collections.singletonList("127.0.0.1");
		final int port = 2345;
		final int maxTotal = 100;
		final int maxWaitMillis = 20;
		final int socketTimeoutInMillis = 50;
		ThriftPool<ThriftExampleService.Client> pool = new ThriftPool<>(new ThriftConnFactory(hosts, port) {
			@Override
			public TServiceClient create() throws Exception {
				HostAndPort hp = getHost();
				TSocket tSocket = new TSocket(hp.getHost(), hp.getPort(), socketTimeoutInMillis);
				TTransport transport = new TFramedTransport(tSocket);
				TProtocol tprotocol = new TBinaryProtocol(transport);
				transport.open();
				ThriftExampleService.Client client = new ThriftExampleService.Client(tprotocol);
				return client;
			}
		}, maxTotal, maxWaitMillis);
		return pool;
	}

	public String test(int id, String message) throws Exception {
		ThriftExampleService.Client client = null;
		boolean broken = false;
		try {
			client = pool.getResource();
			String value = client.test(id, message);
			return value;
		} catch (Exception e) {
			broken = true;
			throw e;
		}
		finally {
			if (client != null) {
				pool.returnResourceObject(client, broken);
			}
		}
	}

	public void close() {
		if (pool != null) {
			pool.close();
		}
	}

 	public static void main(String[] args) {
 		Example instance = new Example();
		try {
			instance.test(0, "a test message");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			instance.close();
		}
	}
}
