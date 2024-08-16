package com.liubs.shadowrpcfly.server.connection;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class ClientChannels {
    private static ClientChannels instance = new ClientChannels();
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);;

    public static ClientChannels getInstance() {
        return instance;
    }

    public ChannelGroup getChannels() {
        return channels;
    }
}
