package com.liubs.shadowrpcfly.client.connection;

import com.liubs.shadowrpcfly.AsyncCall;
import com.liubs.shadowrpcfly.client.handler.ShadowChannelInitializer;
import com.liubs.shadowrpcfly.client.holder.CallBackHolder;
import com.liubs.shadowrpcfly.client.proxy.RemoteServerProxy;
import com.liubs.shadowrpcfly.config.ClientConfig;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.listener.IShadowMessageListener;
import com.liubs.shadowrpcfly.protocol.ShadowMessage;
import com.liubs.shadowrpcfly.protocol.ShadowRPCRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.Consumer;

/**
 * 一个远程连接源，每一个远程服务器节点，即为一个ShadowClient实例
 * @author Liubsyy
 * @date 2023/12/18 10:56 PM
 **/

public class ShadowClient implements IConnection{
    private static final Logger logger = Logger.getLogger(ShadowClient.class);

    private EventLoopGroup group;
    private Channel channel;

    private String remoteIp;
    private int remotePort;

    private ClientConfig config = new ClientConfig();

    public ShadowClient(String host, int port) {
        this(host,port,new NioEventLoopGroup());
    }
    public ShadowClient(String host, int port,EventLoopGroup group) {
        this.remoteIp = host;
        this.remotePort = port;
        this.group = group;
    }

    public void setConfig(ClientConfig config) {
        this.config = config;
    }

    @Override
    public void init(){

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ShadowChannelInitializer(config));

            // 连接到服务器
            ChannelFuture future = bootstrap.connect(remoteIp, remotePort).sync();
            channel = future.channel();

            System.out.printf("连接 %s:%d 成功\n",remoteIp,remotePort);

            Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        } catch (InterruptedException e) {
            e.printStackTrace();
            close();
        }
    }

    @Override
    public <T> T createRemoteProxy(Class<T> serviceStub, String service) {
        return RemoteServerProxy.create(this,serviceStub,service);
    }

    @Override
    public void sendMessage(Object msg) {
        try{
            ShadowRPCRequest request = new ShadowRPCRequest();
            request.setTraceId(IShadowMessageListener.TRACE_ID);

            ShadowMessage message = new ShadowMessage();
            message.setMessageClass(msg.getClass());
            message.setObj(msg);
            request.setParams(new Object[]{message});

            channel.writeAndFlush(request).sync();
        }catch (Exception e) {
            logger.error("发送消息失败"+msg,e);
        }
    }


    /**
     * 异步调用
     * @param asyncCall 处理远程调用
     * @param callBack 回调函数，为null则为同步调用且不回调
     * @param <T>
     */
    public static <T> void asyncCall(AsyncCall asyncCall, Consumer<T> callBack) {
        try{
            CallBackHolder.setCurrentCallBack(callBack);
            asyncCall.run();
        }finally {
            CallBackHolder.removeCurrentCallBack();
        }
    }


    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public Channel getChannel(String group) {
        return channel;
    }


    public void keep(){
        try{
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(){
        try{
            if(null != channel) {
                channel.close();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
