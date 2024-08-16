package com.liubs.shadowrpcfly.server.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class ClientChannels {
    private static ClientChannels instance = new ClientChannels();

    //所有客户端连接
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);;

    //当前连接，仅限于处理服务和消息的环境
    private static ThreadLocal<ChannelHandlerContext> contextChannel = new ThreadLocal<>();

    public static ClientChannels getInstance() {
        return instance;
    }

    public ChannelGroup getChannels() {
        return channels;
    }

    public static ChannelHandlerContext getContextChannel() {
        return contextChannel.get();
    }
    public static void removeContextChannel() {
        contextChannel.remove();
    }
    public static void setContextChannel(ChannelHandlerContext ctx) {
        contextChannel.set(ctx);
    }
}
