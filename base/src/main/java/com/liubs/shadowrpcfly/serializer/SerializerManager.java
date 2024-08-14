package com.liubs.shadowrpcfly.serializer;


import com.liubs.shadowrpcfly.config.BaseConfig;
import com.liubs.shadowrpcfly.logging.Logger;


/**
 * @author Liubsyy
 * @date 2023/12/23 5:01 PM
 **/
public class SerializerManager {
    private static final Logger logger = Logger.getLogger(SerializerManager.class);

    private static SerializerManager instance = new SerializerManager();

    private BaseConfig config;
    private SerializerStrategy serializer = SerializerStrategy.KRYO;

    public static SerializerManager getInstance() {
        return instance;
    }

    //初始化序列化模块
    public void init(String ...packageNames) {

    }


    public void setSerializer(SerializerStrategy serializer) {
        this.serializer = serializer;
    }




}
