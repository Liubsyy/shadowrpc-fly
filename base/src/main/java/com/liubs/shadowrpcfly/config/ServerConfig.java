package com.liubs.shadowrpcfly.config;

import com.liubs.shadowrpcfly.exception.ConfigFieldMissException;

/**
 * @author Liubsyy
 * @date 2024/1/15
 */
public class ServerConfig extends BaseConfig {

    //服务器集群群组，适用于集群模式
    private String group = "DefaultGroup";

    //开放端口
    private int port = 2023;

    //注册中心，如果为空则为单点模式
    private String registryUrl;

    //qps统计开关
    private boolean qpsStat;

    //服务端处理核心线程
    private int serverHandleCorePoolSize = 32;

    //服务端处理最大线程
    private int serverHandleMaxPoolSize = 64;

    //服务端处理线程存活时间：秒
    private int serverHandleKeepAliveSeconds=60;

    //服务端处理队列长度
    private int serverHandleQueueSize=100000;


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isQpsStat() {
        return qpsStat;
    }

    public void setQpsStat(boolean qpsStat) {
        this.qpsStat = qpsStat;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public boolean checkValid(){
        if(null == group || group.isEmpty()) {
            throw new ConfigFieldMissException("group未配置");
        }
        return true;
    }

    public int getServerHandleCorePoolSize() {
        return serverHandleCorePoolSize;
    }

    public void setServerHandleCorePoolSize(int serverHandleCorePoolSize) {
        this.serverHandleCorePoolSize = serverHandleCorePoolSize;
    }

    public int getServerHandleMaxPoolSize() {
        return serverHandleMaxPoolSize;
    }

    public void setServerHandleMaxPoolSize(int serverHandleMaxPoolSize) {
        this.serverHandleMaxPoolSize = serverHandleMaxPoolSize;
    }

    public int getServerHandleKeepAliveSeconds() {
        return serverHandleKeepAliveSeconds;
    }

    public void setServerHandleKeepAliveSeconds(int serverHandleKeepAliveSeconds) {
        this.serverHandleKeepAliveSeconds = serverHandleKeepAliveSeconds;
    }

    public int getServerHandleQueueSize() {
        return serverHandleQueueSize;
    }

    public void setServerHandleQueueSize(int serverHandleQueueSize) {
        this.serverHandleQueueSize = serverHandleQueueSize;
    }
}
