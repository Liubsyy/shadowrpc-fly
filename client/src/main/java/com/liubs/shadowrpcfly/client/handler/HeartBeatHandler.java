package com.liubs.shadowrpcfly.client.handler;

import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.protocol.HeartBeatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * @author Liubsyy
 * @date 2023/12/15 9:35 PM
 **/

public class HeartBeatHandler extends IdleStateHandler {
    private static final Logger logger = Logger.getLogger(HeartBeatHandler.class);

    public HeartBeatHandler(int heartBeatWaitSeconds) {
        super(0, 0, heartBeatWaitSeconds);
    }


    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        logger.debug("发送心跳消息...");
        ctx.writeAndFlush(HeartBeatMessage.SINGLETON);
    }
}
