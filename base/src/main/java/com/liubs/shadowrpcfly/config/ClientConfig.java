package com.liubs.shadowrpcfly.config;

/**
 * @author Liubsyy
 * @date 2024/1/15
 */
public class ClientConfig extends BaseConfig{

    //处理异步响应线程池大小
    private int responsePoolSize=32;

    public int getResponsePoolSize() {
        return responsePoolSize;
    }

    public void setResponsePoolSize(int responsePoolSize) {
        this.responsePoolSize = responsePoolSize;
    }
}
