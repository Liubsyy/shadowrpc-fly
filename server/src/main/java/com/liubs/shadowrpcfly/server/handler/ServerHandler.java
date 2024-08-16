package com.liubs.shadowrpcfly.server.handler;


import com.liubs.shadowrpcfly.listener.IShadowMessageListener;
import com.liubs.shadowrpcfly.listener.ShadowMessageListeners;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.protocol.ShadowMessage;
import com.liubs.shadowrpcfly.server.connection.ClientChannels;
import com.liubs.shadowrpcfly.server.module.ModulePool;
import com.liubs.shadowrpcfly.server.module.SerializeModule;
import com.liubs.shadowrpcfly.constant.ResponseCode;
import com.liubs.shadowrpcfly.protocol.ShadowRPCRequest;
import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;
import com.liubs.shadowrpcfly.server.module.ServerModule;
import com.liubs.shadowrpcfly.server.service.ServiceLookUp;
import com.liubs.shadowrpcfly.server.service.ServiceTarget;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Liubsyy
 * @date 2023/12/3 10:23 PM
 **/
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ServerHandler.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static ExecutorService messageService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private SerializeModule serializeModule = ModulePool.getModule(SerializeModule.class);
    private ServerModule serverModule = ModulePool.getModule(ServerModule.class);



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端{} 已连接",ctx.channel().remoteAddress());
        super.channelActive(ctx);
        ClientChannels.getInstance().getChannels().add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端{} 断开连接",ctx.channel().remoteAddress());
        super.channelInactive(ctx);
        ClientChannels.getInstance().getChannels().remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ShadowRPCRequest request = (ShadowRPCRequest)msg;


        // 打印验证影响速度，压测时去掉
        //logger.info("Server received: " + request.getParams()[0]);

        if(request.getTraceId().equals(IShadowMessageListener.TRACE_ID)) {
            messageService.execute(()->{
                ShadowMessage message = (ShadowMessage)request.getParams()[0];
                ShadowMessageListeners.getInstance().notifyListener(message.getMessageClass(),message.getObj());
            });
            return;
        }

        executorService.execute(()->{
            try {


                ServiceLookUp serviceLookUp = new ServiceLookUp();
                serviceLookUp.setServiceName(request.getServiceName());
                serviceLookUp.setMethodName(request.getMethodName());
                serviceLookUp.setParamTypes(request.getParamTypes());

                ServiceTarget targetRPC = serverModule.getRPC(serviceLookUp);
                Object result = targetRPC.invoke(request.getParams());

                ShadowRPCResponse response = new ShadowRPCResponse();
                response.setTraceId(request.getTraceId());
                response.setCode(ResponseCode.SUCCESS.getCode());
                response.setResult(result);

                // 响应客户端
                ctx.writeAndFlush(response);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
       logger.error("exceptionCaught",cause);
        ctx.close();
    }


}
