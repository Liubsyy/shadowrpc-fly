package com.liubs.shadowrpcfly.server.service;

import com.liubs.shadowrpcfly.config.ServerConfig;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.server.module.ModulePool;
import com.liubs.shadowrpcfly.server.module.SerializeModule;
import com.liubs.shadowrpcfly.server.module.ServerModule;
import com.liubs.shadowrpcfly.server.util.ExecutorUtil;


import java.util.List;

/**
 * @author Liubsyy
 * @date 2023/12/18 10:49 PM
 */
public class ServerManager {
    private static final Logger logger = Logger.getLogger(ServerManager.class);

    private ServerConfig serverConfig;

    private Server server;


    //序列化
    private SerializeModule serializeModule = ModulePool.getModule(SerializeModule.class);

    //服务
    private ServerModule serverModule = ModulePool.getModule(ServerModule.class);

    private List<String> packageNames;


    public List<String> getPackageNames() {
        return packageNames;
    }


    public void setPackageNames(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public SerializeModule getSerializeModule() {
        return serializeModule;
    }

    public void setSerializeModule(SerializeModule serializeModule) {
        this.serializeModule = serializeModule;
    }

    public ServerModule getServerModule() {
        return serverModule;
    }

    public void setServerModule(ServerModule serverModule) {
        this.serverModule = serverModule;
    }

    public ServerManager() {
    }


    public Server start(){

        serverConfig.checkValid();

        //初始化线程池
        ExecutorUtil.init(serverConfig);

        //序列化模块初始化
        serializeModule.init(serverConfig,packageNames);

        //服务初始化
        serverModule.init(serverConfig,packageNames);

        //启动服务
        server = new Server(serverConfig,serverConfig.getGroup(),serverConfig.getPort());

        server.start();

        logger.info("服务器已经启动，端口:{}",serverConfig.getPort());

        return server;
    }



}
