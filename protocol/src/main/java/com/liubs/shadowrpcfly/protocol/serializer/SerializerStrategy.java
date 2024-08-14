package com.liubs.shadowrpcfly.protocol.serializer;

import com.liubs.shadowrpcfly.base.constant.SerializerEnum;
import com.liubs.shadowrpcfly.protocol.entity.JavaSerializeRPCRequest;
import com.liubs.shadowrpcfly.protocol.entity.JavaSerializeRPCResponse;
import com.liubs.shadowrpcfly.protocol.entity.ShadowRPCRequest;
import com.liubs.shadowrpcfly.protocol.entity.ShadowRPCResponse;
import com.liubs.shadowrpcfly.protocol.model.IModelParser;
import com.liubs.shadowrpcfly.protocol.serializer.javaserializer.JavaModelParser;
import com.liubs.shadowrpcfly.protocol.serializer.javaserializer.JavaSerializer;
import com.liubs.shadowrpcfly.protocol.serializer.kryo.KryoModelParser;
import com.liubs.shadowrpcfly.protocol.serializer.kryo.KryoSerializer;

/**
 * @author Liubsyy
 * @date 2023/12/23 6:36 PM
 **/
public enum SerializerStrategy {

    KRYO(SerializerEnum.KRYO,
            new KryoSerializer(),
            ShadowRPCRequest.class,
            ShadowRPCResponse.class,
            new KryoModelParser()
            ),


    JAVA_SERIALISE(SerializerEnum.JAVA_SERIALISE,
            new JavaSerializer(),
            JavaSerializeRPCRequest.class,
            JavaSerializeRPCResponse.class,
            new JavaModelParser()
    ),

    ;

    private SerializerEnum serializerEnum;
    private ISerializer serializer;
    private Class<?> requestClass;
    private Class<?> responseClass;

    private IModelParser modelParser;


    SerializerStrategy(SerializerEnum serializerEnum,ISerializer serializer, Class<?> requestClass, Class<?> responseClass, IModelParser modelParser) {
        this.serializerEnum = serializerEnum;
        this.serializer = serializer;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.modelParser = modelParser;
    }


    public ISerializer getSerializer() {
        return serializer;
    }

    public Class<?> getRequestClass() {
        return requestClass;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }

    public IModelParser getModelParser() {
        return modelParser;
    }



    public static SerializerStrategy findBySerializer(SerializerEnum serializerEnum) {
        for(SerializerStrategy e : values()) {
            if(e.serializerEnum == serializerEnum) {
                return e;
            }
        }
        return null;
    }

}
