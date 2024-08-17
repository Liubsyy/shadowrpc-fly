package com.liubs.shadowrpcfly.client.holder;


import com.liubs.shadowrpcfly.config.ClientConfig;
import com.liubs.shadowrpcfly.listener.IShadowMessageListener;
import com.liubs.shadowrpcfly.listener.ShadowMessageListeners;
import com.liubs.shadowrpcfly.protocol.ShadowMessage;
import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Liubsyy
 * @date 2023/12/3 11:32 PM
 **/
public class ReceiveHolder {
    public Map<String, CompletableFuture<Object>> futureMap = new ConcurrentHashMap<>();

    private static ExecutorService responseService ;
    private static ExecutorService messageService;

    private static ReceiveHolder instance = new ReceiveHolder();
    public static ReceiveHolder getInstance() {
        return instance;
    }

    public static void initExecutor(ClientConfig config) {
        if(null == responseService) {
            synchronized (ReceiveHolder.class) {
                if(null == responseService) {
                    responseService = Executors.newFixedThreadPool(config.getResponsePoolSize());
                    messageService = Executors.newFixedThreadPool(config.getMessagePoolSize());
                }
            }
        }
    }

    public Future<?> initFuture(String uuid){
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        futureMap.put(uuid,completableFuture);
        return completableFuture;
    }

    public void deleteWait(String uuid) {
        CompletableFuture<Object> remove = futureMap.remove(uuid);
        if(null != remove) {
            remove.cancel(true);
        }
    }


    public void receiveData(ShadowRPCResponse response){
        if(null == response.getTraceId()) {
            return;
        }
        if(response.getTraceId().equals(IShadowMessageListener.TRACE_ID)) {
            messageService.execute(()->{
                ShadowMessage message = (ShadowMessage)response.getResult();
                ShadowMessageListeners.getInstance().notifyListener(message.getMessageClass(),message.getObj());
            });
            return;
        }

        CompletableFuture<Object> future = futureMap.remove(response.getTraceId());
        if(null != future) {
            //这个通知不要用线程池反而更快
            future.complete(response);
        }else {
            responseService.execute(()->{
                CallBackHolder.getInstance().asyncCallBack(response);
            });
        }
//        executorService.execute(()->{
//            if(null != future) {
//                future.complete(response);
//            }else {
//                CallBackHolder.getInstance().asyncCallBack(response);
//            }
//        });

    }

}
