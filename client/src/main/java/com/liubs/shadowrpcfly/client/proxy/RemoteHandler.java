package com.liubs.shadowrpcfly.client.proxy;

import com.liubs.shadowrpcfly.client.connection.IConnection;
import com.liubs.shadowrpcfly.client.holder.CallBackHolder;
import com.liubs.shadowrpcfly.client.holder.ReceiveHolder;
import com.liubs.shadowrpcfly.exception.SendException;
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
import java.util.function.Consumer;

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
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        String methodName = method.getName();
        if ("hashCode".equals(methodName)) {
            return System.identityHashCode(proxy);
        }else if ("equals".equals(methodName)) {
            return proxy == args[0];
        } else if ("toString".equals(methodName)) {
            return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
        }

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
            throw new SendException("服务器已关闭,发送消息抛弃");
        }

        Consumer<Object> callBack = CallBackHolder.getCurrentCallBack();
        Future<?> future = null == callBack ? ReceiveHolder.getInstance().initFuture(traceId) : null;
        try{
            channel.writeAndFlush(request).sync();
        }catch (Exception e) {
            logger.error("发送请求{}失败",traceId);
            ReceiveHolder.getInstance().deleteWait(traceId);
            throw new SendException("服务器已关闭,发送失败");
        }

        if(null != callBack) {
            //异步函数直接等待回调用
            CallBackHolder.getInstance().addCallBack(traceId,callBack);
            return null;
        }

        try{
            ShadowRPCResponse responseModel = (ShadowRPCResponse)future.get(3, TimeUnit.SECONDS);
            return responseModel.getResult();
        }catch (TimeoutException timeoutException) {
            logger.error("超时请求,抛弃消息{}",traceId);
            ReceiveHolder.getInstance().deleteWait(traceId);
            throw new TimeoutException("请求超时 "+traceId);
        }
    }
}
