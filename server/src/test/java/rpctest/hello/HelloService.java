package rpctest.hello;

import com.liubs.shadowrpcfly.listener.ShadowMessageListeners;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.server.annotation.ShadowService;
import com.liubs.shadowrpcfly.config.ServerConfig;
import com.liubs.shadowrpcfly.server.init.ServerBuilder;
import com.liubs.shadowrpcfly.server.service.Server;
import org.junit.Test;
import rpctest.entity.MyMessage;

/**
 * 一个helloservice服务
 * @author Liubsyy
 * @date 2023/12/18 11:01 PM
 **/
@ShadowService(serviceName = "helloservice")
public class HelloService implements IHello {
    private static final Logger logger = Logger.getLogger(HelloService.class);


    public void test(){
        throw new RuntimeException("此函数不会被客户端检测到，也无法调用");
    }

    @Override
    public String hello(String msg) {
        return "Hello,"+msg;
    }

    @Override
    public String helloSlowly(String msg) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Slowly hello,"+msg;
    }

    @Override
    public String helloForBroadcast(String msg){

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                MyMessage message = new MyMessage();
                message.setNum(3);
                message.setContent("敌军还有3秒到底战场");
                Server.broadcastMessage(message);

                Thread.sleep(1000);
                message.setNum(2);
                message.setContent("敌军还有2秒到底战场");
                Server.broadcastMessage(message);

                Thread.sleep(1000);
                message.setNum(1);
                message.setContent("敌军还有1秒到底战场");
                Server.broadcastMessage(message);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        MyMessage message = new MyMessage();
        message.setContent("小道消息：老炮是卧底...");
        Server.sendMessage(message);

        return "等待广播,"+msg;
    }


    @Override
    public MyMessage say(MyMessage message) {
        MyMessage message1 = new MyMessage();
        message1.setContent("hello received "+"("+message.getContent()+")");
        message1.setNum(message.getNum()+1);
        return message1;
    }


    //启动服务端
    @Test
    public void helloServiceStart() {

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setQpsStat(true); //统计qps
        serverConfig.setPort(2023);

        //监听消息
        ShadowMessageListeners.getInstance().<MyMessage>addListener(MyMessage.class,
                message-> {
                    logger.info("收到客户端消息:"+message.getContent());
                    MyMessage reMsg = new MyMessage();
                    reMsg.setContent("收到支援请求，正在增兵...");
                    Server.sendMessage(reMsg);
                });

        ServerBuilder.newBuilder()
                .serverConfig(serverConfig)
                .addPackage("rpctest.hello")
                .build()
                .start()
                .keep();

    }

}
