package com.liubs.shadowrpcfly.exception;

/**
 * 发送失败消息
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class SendException extends RuntimeException{
    public SendException(String message) {
        super(message);
    }
}
