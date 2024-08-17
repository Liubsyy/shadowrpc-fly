package com.liubs.shadowrpcfly.server.service;

import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.Method;

/**
 * @author Liubsyy
 * @date 2023/12/28
 **/
public class ServiceTarget {
    private FastClass fastClass;
    private Object targetObj;
    private Method method;
    private final int methodIndex;

    public ServiceTarget(Object targetObj, Method method) {
        this.targetObj = targetObj;
        this.method = method;
        this.fastClass = FastClass.create(targetObj.getClass());
        this.methodIndex = fastClass.getIndex(method.getName(), method.getParameterTypes());
    }

    public Object getTargetObj() {
        return targetObj;
    }

    public void setTargetObj(Object targetObj) {
        this.targetObj = targetObj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object call(Object [] params) throws Throwable {

        // 使用cglib代理
        return fastClass.invoke(methodIndex, targetObj, params);

//        return method.invoke(targetObj,params);
    }


}
