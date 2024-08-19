package com.liubs.shadowrpcfly.server.util;


import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.server.annotation.ShadowServiceHolder;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author Liubsyy
 * @date 2023/12/12 21:54
 */
public class AnnotationScanner {
    private static final Logger logger = Logger.getLogger(AnnotationScanner.class);

    public static <T extends Annotation>  List<ShadowServiceHolder<T>> scanAnnotations(String packageName, Class<T> annotation) throws IOException {

        List<ShadowServiceHolder<T>> allResults = new ArrayList<>();

        Set<Class<?>> classes = PackageScanUtil.scanClasses(packageName, annotation);
        classes.forEach(clazz->{
            T shadowServiceAnno = clazz.getAnnotation(annotation);
            if (shadowServiceAnno != null) {
                allResults.add(new ShadowServiceHolder<>(shadowServiceAnno, clazz));
            }
        });

        logger.info("scanAnnotations="+allResults);
        return allResults;
    }

}
