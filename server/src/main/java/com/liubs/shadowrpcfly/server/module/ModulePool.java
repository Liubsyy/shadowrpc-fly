package com.liubs.shadowrpcfly.server.module;

import com.liubs.shadowrpcfly.server.annotation.ModuleInject;
import com.liubs.shadowrpcfly.server.annotation.ShadowModule;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.server.util.PackageScanUtil;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Liubsyy
 * @date 2024/1/15
 */
public class ModulePool {
    private static final Logger logger = Logger.getLogger(ModulePool.class);

    private static Map<Class<?>,IModule> moduleMap = new HashMap<>();

    static {
        iniModules();
    }

    public static void iniModules(){
        Set<Class<?>> classes = PackageScanUtil.scanClasses("com.liubs.shadowrpcfly", ShadowModule.class);
        classes.forEach(c->{
            try {
                IModule o = (IModule)c.newInstance();
                moduleMap.put(c,o);
            } catch (Exception e) {
                logger.error(String.format("create module %s fail",c),e);
                throw new RuntimeException(e);
            }
        });

        for(Map.Entry<Class<?>, IModule> entry : moduleMap.entrySet()) {
            Class<?> key = entry.getKey();
            IModule value = entry.getValue();

            for(Field field : key.getDeclaredFields()){
                if(null == field.getAnnotation(ModuleInject.class)) {
                    continue;
                }
                boolean accessible = field.isAccessible();
                try{
                    field.setAccessible(true);
                    field.set(value,moduleMap.get(field.getType()));
                } catch (IllegalAccessException e) {
                    logger.error(String.format("module value {} set failed %s fail",field.getType()),e);
                } finally {
                    field.setAccessible(accessible);
                }
            }
        }

    }

    public static <T extends IModule> T getModule(Class<T> moduleClass) {
        return (T)moduleMap.get(moduleClass);
    }

}
