package com.liubs.shadowrpcfly.client.holder;


import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Liubsyy
 * @date 2023/12/3 11:32 PM
 **/
public class ReceiveHolder {
    public Map<String, CompletableFuture<Object>> futureMap = new ConcurrentHashMap<>();

    private static ReceiveHolder instance = new ReceiveHolder();
    public static ReceiveHolder getInstance() {
        return instance;
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
        CompletableFuture<Object> future = futureMap.remove(response.getTraceId());
        if(null != future) {
            future.complete(response);
        }else {
            CallBackHolder.getInstance().asyncCallBack(response);
        }
    }

}
