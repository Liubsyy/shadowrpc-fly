package com.liubs.shadowrpcfly.server;

import com.liubs.shadowrpcfly.base.annotation.ModuleInject;
import com.liubs.shadowrpcfly.base.annotation.ShadowModule;
import com.liubs.shadowrpcfly.base.annotation.ShadowService;
import com.liubs.shadowrpcfly.base.annotation.ShadowServiceHolder;
import com.liubs.shadowrpcfly.config.ServerConfig;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.base.module.IModule;
import com.liubs.shadowrpcfly.protocol.SerializeModule;
import com.liubs.shadowrpcfly.server.util.AnnotationScanner;
import com.liubs.shadowrpcfly.server.service.ServiceLookUp;
import com.liubs.shadowrpcfly.server.service.ServiceTarget;


import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liubsyy
 * @date 2024/1/16
 */
@ShadowModule
public class ServerModule implements IModule {
    private static final Logger logger = Logger.getLogger(ServerModule.class);

    @ModuleInject
    private SerializeModule serializeModule;

    private ServerConfig serverConfig;

    //所有服务
    private Map<ServiceLookUp,ServiceTarget> allRPC = new ConcurrentHashMap<>();

    public void init(ServerConfig serverConfig,List<String> packages) {
        this.serverConfig = serverConfig;
        //初始化服务
        List<ShadowServiceHolder<ShadowService>> shadowServices = new ArrayList<>();

        for(String packageName : packages) {
            try {
                shadowServices.addAll(AnnotationScanner.scanAnnotations(packageName, ShadowService.class));
            } catch (IOException e) {
                logger.error("scanService err",e);
            }
        }

        for(ShadowServiceHolder<ShadowService> ShadowServiceHolder : shadowServices) {
            ShadowService serviceAnnotation = ShadowServiceHolder.getAnnotation();
            Class<?> serviceClass = ShadowServiceHolder.getClassz();
            try {
                Object o = serviceClass.newInstance();


                for(Method method : serviceClass.getMethods()) {

                    if(Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers())){
                        continue;
                    }

                    ServiceLookUp serviceLookUp = new ServiceLookUp();
                    serviceLookUp.setServiceName(serviceAnnotation.serviceName());
                    serviceLookUp.setMethodName(method.getName());
                    serviceLookUp.setParamTypes(method.getParameterTypes());

                    ServiceTarget serviceTarget = new ServiceTarget();
                    serviceTarget.setTargetObj(o);
                    serviceTarget.setMethod(method);
                    addRPCInterface(serviceLookUp,serviceTarget);
                }

            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addRPCInterface(ServiceLookUp lookUp,ServiceTarget obj) {
        allRPC.put(lookUp,obj);
    }

    public ServiceTarget getRPC(ServiceLookUp lookUp) {
        return allRPC.get(lookUp);
    }



}
