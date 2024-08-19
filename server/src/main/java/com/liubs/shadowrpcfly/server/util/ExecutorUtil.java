package com.liubs.shadowrpcfly.server.util;

import com.liubs.shadowrpcfly.config.ServerConfig;
import com.liubs.shadowrpcfly.server.handler.ServerHandler;

import java.util.concurrent.*;

/**
 * @author Liubsyy
 * @date 2024/8/18
 **/
public class ExecutorUtil {

    /**
     * 逻辑处理线程池
     */
    private static ExecutorService executorService;

    /**
     * 处理消息推送线程池
     */
    private static ExecutorService messageService;


    public static void init(ServerConfig serverConfig) {
        if(null ==executorService){
            synchronized (ServerHandler.class) {
                if(null == executorService) {
                    executorService = new ThreadPoolExecutor(serverConfig.getServerHandleCorePoolSize(),
                            serverConfig.getServerHandleMaxPoolSize(),
                            serverConfig.getServerHandleKeepAliveSeconds(), TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(serverConfig.getServerHandleQueueSize()));
                    messageService = Executors.newFixedThreadPool(serverConfig.getMessagePoolSize());
                }
            }
        }
    }


    public static void executeService(Runnable runnable) {
        executorService.execute(runnable);
    }

    public static void executeMessage(Runnable runnable) {
        messageService.execute(runnable);
    }
}
