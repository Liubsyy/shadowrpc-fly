package com.liubs.shadowrpcfly.listener;

import com.liubs.shadowrpcfly.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liubsyy
 * @date 2024/8/16
 **/
public class ShadowMessageListeners {
    private Logger logger = Logger.getLogger(ShadowMessageListeners.class);

    private static ShadowMessageListeners instance = new ShadowMessageListeners();
    private Map<Class<?>, List<IShadowMessageListener<Object>>> listenersMap = new HashMap<>();

    public static ShadowMessageListeners getInstance() {
        return instance;
    }

    public <T> ShadowMessageListeners addListener(final Class<?> message, IShadowMessageListener<T> listener){
        synchronized (message) {
            List<IShadowMessageListener<Object>> consumers = listenersMap.computeIfAbsent(message, k -> new ArrayList<>());
            consumers.add((IShadowMessageListener<Object>)listener);
        }
        return this;
    }

    public void notifyListener(Class<?> message,Object notice){
        List<IShadowMessageListener<Object>> listeners = listenersMap.get(message);
        for(IShadowMessageListener<Object> listener : listeners) {
            try{
                listener.handle(notice);
            }catch (Throwable e) {
                logger.error("notify listener err"+message,e);
            }
        }
    }
}
