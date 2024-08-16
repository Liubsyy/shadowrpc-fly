package com.liubs.shadowrpcfly.listener;

/**
 * 监听消息
 * @author Liubsyy
 * @date 2024/8/16
 *
 **/
public interface IShadowMessageListener<T> {
    String TRACE_ID="SHADOW_MSG";

    void handle(T msg);
}
