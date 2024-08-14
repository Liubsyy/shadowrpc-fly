package com.liubs.shadowrpc.protocol;

import com.liubs.shadowrpc.base.annotation.ShadowModule;
import com.liubs.shadowrpc.base.config.BaseConfig;
import com.liubs.shadowrpc.base.constant.SerializerEnum;
import com.liubs.shadowrpc.base.module.IModule;
import com.liubs.shadowrpc.protocol.serializer.SerializerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Liubsyy
 * @date 2024/1/15
 */

@ShadowModule
public class SerializeModule implements IModule {
    private static final Logger logger = LoggerFactory.getLogger(SerializeModule.class);


    //配置
    private BaseConfig config;

    //序列化方式
    private SerializerStrategy serializer;

    private void init(BaseConfig baseConfig) {
        this.config = baseConfig;
        SerializerEnum serializerEnum = SerializerEnum.findByType(baseConfig.getSerializer());
        if(null == serializerEnum) {
            throw new RuntimeException("Cannot find serializer:"+baseConfig.getSerializer());
        }
        this.serializer = SerializerStrategy.findBySerializer(serializerEnum);
    }


    public void init(BaseConfig baseConfig, List<String> packages) {
        init(baseConfig);


    }

    public SerializerStrategy getSerializer() {
        return serializer;
    }

    public byte[] serialize(Object obj) {
        return serializer.getSerializer().serialize(obj);
    }

    public <T> T deserializeRequest(byte[] bytes) {
        return (T)serializer.getSerializer().deserialize(bytes, serializer.getRequestClass());
    }

    public <T> Object deserializeResponse(byte[] bytes) {
        return (T)serializer.getSerializer().deserialize(bytes,serializer.getResponseClass());
    }




}
