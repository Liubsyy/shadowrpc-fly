package com.liubs.shadowrpcfly.protocol.serializer;

import com.liubs.shadowrpcfly.constant.SerializerEnum;
import com.liubs.shadowrpcfly.protocol.ShadowRPCRequest;
import com.liubs.shadowrpcfly.protocol.ShadowRPCResponse;
import com.liubs.shadowrpcfly.protocol.serializer.kryo.KryoSerializer;

/**
 * @author Liubsyy
 * @date 2023/12/23 6:36 PM
 **/
public enum SerializerStrategy {

    KRYO(SerializerEnum.KRYO,
            new KryoSerializer(),
            ShadowRPCRequest.class,
            ShadowRPCResponse.class
            )


    ;

    private SerializerEnum serializerEnum;
    private ISerializer serializer;
    private Class<?> requestClass;
    private Class<?> responseClass;



    SerializerStrategy(SerializerEnum serializerEnum,ISerializer serializer, Class<?> requestClass, Class<?> responseClass) {
        this.serializerEnum = serializerEnum;
        this.serializer = serializer;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
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




    public static SerializerStrategy findBySerializer(SerializerEnum serializerEnum) {
        for(SerializerStrategy e : values()) {
            if(e.serializerEnum == serializerEnum) {
                return e;
            }
        }
        return null;
    }

}
