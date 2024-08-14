package com.liubs.shadowrpcfly.server.service;

import com.liubs.shadowrpcfly.config.ServerConfig;
import com.liubs.shadowrpcfly.server.handler.ShadowChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Liubsyy
 * @date 2023/12/4 11:59 PM
 **/
public class Server {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    /**
     * 这个group主要是区分不同的集群，比如商品集群，订单集群，属于不同的group，在zk中注册不同的服务分组
     */
    private String group;
    private int port;

    private ServerConfig serverConfig;


    public Server(ServerConfig serverConfig,String group, int port) {
        this.serverConfig = serverConfig;
        this.group = group;
        this.port = port;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    public void start(){
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ShadowChannelInitializer(serverConfig))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            channelFuture = bootstrap.bind(port).sync();
            System.out.println("服务器启动成功...");

            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        } catch (InterruptedException e) {
            stop();
            throw new RuntimeException(e);
        }
    }






    public void keep(){
        try{
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //优雅的退出
    public void stop(){
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
