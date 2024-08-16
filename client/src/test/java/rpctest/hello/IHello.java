package rpctest.hello;

import com.liubs.shadowrpcfly.annotation.ShadowInterface;
import rpctest.entity.MyMessage;

/**
 * @author Liubsyy
 * @date 2023/12/18 10:59 PM
 **/
@ShadowInterface
public interface IHello {
    String hello(String msg);
    String helloSlowly(String msg);
    String helloForBroadcast(String msg);
    MyMessage say(MyMessage message);
}
