package com.liubs.shadowrpcfly.client.connection;

import com.liubs.shadowrpcfly.client.exception.ConnectTimeoutException;
import com.liubs.shadowrpcfly.client.handler.RequestHandler;
import com.liubs.shadowrpcfly.client.handler.ResponseHandler;
import com.liubs.shadowrpcfly.client.nio.MessageSendFuture;
import com.liubs.shadowrpcfly.client.nio.NIOClient;
import com.liubs.shadowrpcfly.client.nio.NIOConfig;

import java.io.IOException;

/**
 * @author Liubsyy
 * @date 2024/1/20
 **/
public class ShadowClient {

    private String host;
    private int port;

    private NIOClient nioClient;
    private RequestHandler requestHandler;
    private ResponseHandler responseHandler;

    private SimpleHeartBeat simpleHeartBeat;

    public ShadowClient(String host, int port) {
       this(host,port,new NIOConfig());
    }

    public ShadowClient(String host, int port, NIOConfig nioConfig) {
        this.host = host;
        this.port = port;
        this.requestHandler = new RequestHandler(nioConfig.getSerializer());
        this.responseHandler = new ResponseHandler(nioConfig.getSerializer());
        this.nioClient = new NIOClient(host,port,nioConfig,responseHandler);
    }

    public void connect() throws IOException, ConnectTimeoutException {
        nioClient.connect();

        if(nioClient.getNioConfig().isHearBeat()) {
            this.simpleHeartBeat = new SimpleHeartBeat(this,nioClient.getNioConfig().getHeartBeatWaitSeconds());
            this.simpleHeartBeat.start();
        }
    }

    public NIOConfig getNIOConfig(){
        return nioClient.getNioConfig();
    }

    public <T> T createRemoteProxy(Class<T> serviceStub, String serviceName) {
        return RemoteProxy.create(this,serviceStub,serviceName);
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public MessageSendFuture sendMessage(byte[] bytes) {
        return nioClient.sendMessage(bytes);
    }

    public boolean isRunning(){
        return nioClient.isRunning();
    }

    public void close(){
        nioClient.close();
        if(null !=simpleHeartBeat && simpleHeartBeat.isAlive()) {
            simpleHeartBeat.interrupt();
        }
    }


}
