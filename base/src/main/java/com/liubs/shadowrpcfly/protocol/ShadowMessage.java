package com.liubs.shadowrpcfly.protocol;

import com.liubs.shadowrpcfly.annotation.ShadowEntity;
import com.liubs.shadowrpcfly.annotation.ShadowField;

/**
 * @author Liubsyy
 * @date 2024/8/16
 **/
@ShadowEntity
public class ShadowMessage {

    @ShadowField(1)
    private Class<?> messageClass;

    @ShadowField(2)
    private Object obj;

    public Class<?> getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(Class<?> messageClass) {
        this.messageClass = messageClass;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
