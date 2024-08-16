package com.liubs.shadowrpcfly.client.connection;

import io.netty.channel.Channel;


/**
 * @author Liubsyy
 * @date 2024/1/17
 **/
public interface IConnection {
    void init();

    /**
     * 创建RPC接口代理
     * @param serviceStub
     * @param service
     * @return
     * @param <T>
     */
    <T> T createRemoteProxy(Class<T> serviceStub,String service);

    /**
     * 发送消息
     * @param msg
     */
    void sendMessage(Object msg);

    Channel getChannel(String group);

    void close();
}
