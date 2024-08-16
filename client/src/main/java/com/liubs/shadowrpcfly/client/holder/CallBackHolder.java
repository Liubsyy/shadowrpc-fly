package com.liubs.shadowrpcfly.client.holder;

import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Liubsyy
 * @date 2024/8/16
 **/
public class CallBackHolder {
    private static CallBackHolder instance = new CallBackHolder();

    private static ThreadLocal<Consumer<Object>> callBackThreadLocal = new ThreadLocal<>();

    /**
     * 异步回调的函数
     */
    private Map<String, Consumer<Object>> callBackMap = new ConcurrentHashMap<>();

    public static CallBackHolder getInstance() {
        return instance;
    }


    public static void setCurrentCallBack(Consumer<?> callBack) {
        callBackThreadLocal.set((Consumer<Object>)callBack);
    }

    public static Consumer<Object> getCurrentCallBack() {
        return callBackThreadLocal.get();
    }

    public static void removeCurrentCallBack(){
        callBackThreadLocal.remove();
    }



    public void addCallBack(String uuid,Consumer<Object> consumer){
        callBackMap.put(uuid,consumer);
    }

    public void asyncCallBack(ShadowRPCResponse response){
        if(null == response.getTraceId()) {
            return;
        }
        Consumer<Object> consumer = callBackMap.remove(response.getTraceId());
        if(null != consumer) {
            consumer.accept(response.getResult());
        }
    }





}
