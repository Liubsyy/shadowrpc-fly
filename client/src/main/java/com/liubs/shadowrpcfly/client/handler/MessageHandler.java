package com.liubs.shadowrpcfly.client.handler;

import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.protocol.HeartBeatMessage;
import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;
import com.liubs.shadowrpcfly.serializer.ISerializer;
import com.liubs.shadowrpcfly.serializer.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 消息的压缩和解压缩
 * @author Liubsyy
 * @date 2023/12/15 10:01 PM
 **/
public class MessageHandler extends ByteToMessageCodec<Object> {

    private static final Logger logger = Logger.getLogger(MessageHandler.class);
    private static ISerializer serialize = new KryoSerializer();


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data;
        if(msg instanceof HeartBeatMessage) {
            //心跳消息
            data = HeartBeatMessage.getHearBeatMsg();
        }else {
            data = serialize.serialize(msg);
        }

        int dataLength = data.length;
        out.writeInt(dataLength); // 先写入消息长度
        out.writeBytes(data); // 写入序列化后的数据
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if(readableBytes <= 0){
            return;
        }
        byte[] data = new byte[readableBytes];

        in.readBytes(data);

        if(HeartBeatMessage.isHeartBeatMsg(data)) {
            logger.info("收到心跳消息...");
        }else {
            Object obj = serialize.deserialize(data, ShadowRPCResponse.class);
            out.add(obj);
        }
    }

}
