package com.liubs.shadowrpcfly.server.handler;


import com.liubs.shadowrpcfly.base.logging.Logger;
import com.liubs.shadowrpcfly.base.module.ModulePool;
import com.liubs.shadowrpcfly.protocol.SerializeModule;
import com.liubs.shadowrpcfly.protocol.constant.ResponseCode;
import com.liubs.shadowrpcfly.protocol.entity.ShadowRPCRequest;
import com.liubs.shadowrpcfly.protocol.entity.ShadowRPCResponse;
import com.liubs.shadowrpcfly.server.ServerModule;
import com.liubs.shadowrpcfly.server.service.ServiceLookUp;
import com.liubs.shadowrpcfly.server.service.ServiceTarget;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Liubsyy
 * @date 2023/12/3 10:23 PM
 **/
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ServerHandler.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private SerializeModule serializeModule = ModulePool.getModule(SerializeModule.class);
    private ServerModule serverModule = ModulePool.getModule(ServerModule.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端{} 已连接",ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端{} 断开连接",ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 打印验证影响速度，压测时去掉
        //logger.info("Server received: " + msg);

        ShadowRPCRequest request = (ShadowRPCRequest)msg;

        //System.out.println("Server received: " + requestModel.getParams()[0]);
        executorService.execute(()->{
            try {

                ServiceLookUp serviceLookUp = new ServiceLookUp();
                serviceLookUp.setServiceName(request.getServiceName());
                serviceLookUp.setMethodName(request.getMethodName());
                serviceLookUp.setParamTypes(request.getParamTypes());
                ServiceTarget targetRPC = serverModule.getRPC(serviceLookUp);

                Object result = targetRPC.invoke(request.getParams());

                ShadowRPCResponse response = new ShadowRPCResponse();
                response.setTraceId(response.getTraceId());
                response.setCode(ResponseCode.SUCCESS.getCode());
                response.setResult(result);

                // 响应客户端
                ctx.writeAndFlush(response);
            } catch (Exception e) {
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
