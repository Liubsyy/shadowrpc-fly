package com.liubs.shadowrpcfly.serializer;


/**
 * @author Liubsyy
 * @date 2023/12/18 10:42 PM
 **/
public interface ISerializer {

    byte[] serialize(Object object);

    <T> T deserialize(byte[] array, Class<T> clazz);

}
