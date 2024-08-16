package com.liubs.shadowrpcfly.client.connection;

import io.netty.channel.Channel;

import java.util.function.Consumer;

/**
 * @author Liubsyy
 * @date 2024/1/17
 **/
public interface IConnection {
    void init();

    <T> T createRemoteProxy(Class<T> serviceStub,String service);


    Channel getChannel(String group);

    void close();
}
