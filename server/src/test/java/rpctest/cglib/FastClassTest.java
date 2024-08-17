package rpctest.cglib;

import net.sf.cglib.reflect.FastClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射和FastClass性能对比
 * FastClass略优，差别不是很大
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class FastClassTest {

    public static final int TIMES = 100000000;

    public static Method method1;
    public static Service1 service1 = new Service1(1);

    public static Method method2;
    public static Service2 service2 = new Service2(2);

    public static FastClass fastClass;
    public static int method2Index ;

    @BeforeClass
    public static void init(){
        try {
            fastClass = FastClass.create(Service2.class);
            method1 = Service1.class.getMethod("action", int.class);
            method2 = Service2.class.getMethod("action", int.class);
            method2Index = fastClass.getIndex(method2.getName(), method2.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反射用时1:2607ms
     * 反射用时2:2104ms
     */
    @Test
    public void reflect() throws InvocationTargetException, IllegalAccessException {
        long time1 = System.currentTimeMillis();
        reflect(TIMES);
        long time2 = System.currentTimeMillis();
        reflect(TIMES);
        long time3 = System.currentTimeMillis();

        System.out.println("反射用时1:"+(time2-time1)+"ms");
        System.out.println("反射用时2:"+(time3-time2)+"ms");
    }

    private void reflect(int times) throws InvocationTargetException, IllegalAccessException {
        for(int i = 0;i<times ;i++) {
            method1.invoke(service1,new Object[]{i});
        }
    }

    /**
     cglib用时1:2511ms
     cglib用时2:1940ms
     */
    @Test
    public void fastClass() throws InvocationTargetException {
        long time1 = System.currentTimeMillis();
        fastClass(TIMES);
        long time2 = System.currentTimeMillis();
        fastClass(TIMES);
        long time3 = System.currentTimeMillis();

        System.out.println("cglib用时1:"+(time2-time1)+"ms");
        System.out.println("cglib用时2:"+(time3-time2)+"ms");
    }
    public void fastClass(int times) throws InvocationTargetException {
        for(int i = 0;i<times ;i++) {
            fastClass.invoke(method2Index, service2, new Object[]{i});
        }
    }

    /**
     * 先预热，再对比差异
     * 反射用时1:2004ms
     * 反射用时2:2066ms
     * cglib用时1:1993ms
     * cglib用时2:1805ms
     */
    @Test
    public void compareReflectAndFastClass() throws InvocationTargetException, IllegalAccessException {

        //预热
        reflect(TIMES);
        fastClass(TIMES);

        //开始对比
        reflect();
        fastClass();
    }

    /**
     * 先预热，再对比差异
     * cglib用时1:1942ms
     * cglib用时2:1717ms
     * 反射用时1:2017ms
     * 反射用时2:2040ms
     */
    @Test
    public void compareReflectAndFastClass2() throws InvocationTargetException, IllegalAccessException {

        //预热
        fastClass(TIMES);
        reflect(TIMES);

        //开始对比
        fastClass();
        reflect();
    }


}
