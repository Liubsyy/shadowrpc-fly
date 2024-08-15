package com.liubs.shadowrpcfly.client.proxy;

import com.liubs.shadowrpcfly.client.connection.IConnection;
import com.liubs.shadowrpcfly.client.handler.ReceiveHolder;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.protocol.ShadowRPCRequest;
import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Liubsyy
 * @date 2023/12/31
 **/
public class RemoteHandler implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(RemoteHandler.class);

    /**
     * 如果不使用注册中心，则必须有ShadowClient
     */
    private IConnection clientConnection;

    /**
     * 远程接口stub
     */
    private  Class<?> serviceStub;


    /**
     * 集群
     */
    private String group;

    /**
     * 服务名
     */
    private String serviceName;

    public RemoteHandler(IConnection client, Class<?> serviceStub, String group,String serviceName) {
        this.clientConnection = client;
        this.serviceStub = serviceStub;
        this.group = group;
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try{
            ShadowRPCRequest request = new ShadowRPCRequest();
            String traceId = UUID.randomUUID().toString();
            request.setTraceId(traceId);
            request.setServiceName(serviceName);
            request.setMethodName(method.getName());
            request.setParamTypes(method.getParameterTypes());
            request.setParams(args);

            Channel channel = clientConnection.getChannel(group);

            if(!channel.isOpen()) {
                logger.error("服务器已关闭,发送消息抛弃...");
                return null;
            }

            Future<?> future = ReceiveHolder.getInstance().initFuture(traceId);
            try{
                channel.writeAndFlush(request).sync();
            }catch (Exception e) {
                logger.error("发送请求{}失败",traceId);
                ReceiveHolder.getInstance().deleteWait(traceId);
                return null;
            }

            try{
                ShadowRPCResponse responseModel = (ShadowRPCResponse)future.get(3, TimeUnit.SECONDS);
                return responseModel.getResult();
            }catch (TimeoutException timeoutException) {
                logger.error("超时请求,抛弃消息{}",traceId);
                ReceiveHolder.getInstance().deleteWait(traceId);
                return null;
            }

        }catch (Throwable e) {
            logger.error("invoke err",e);
        }

        return null;
    }
}
