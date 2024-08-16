package com.liubs.shadowrpcfly.client.handler;

import com.liubs.shadowrpcfly.client.holder.ReceiveHolder;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Liubsyy
 * @date 2023/12/3 10:29 PM
 **/


public class ClientHandler extends ChannelInboundHandlerAdapter{
    private static final Logger logger = Logger.getLogger(ClientHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("已连接远程{}",ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("远程{} 已断开",ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ReceiveHolder.getInstance().receiveData((ShadowRPCResponse) msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 在发生异常时执行
       logger.error("exceptionCaught err",cause);
        ctx.close();
    }



}

