package rpctest.hello;

import com.liubs.shadowrpcfly.client.connection.ShadowClient;
import com.liubs.shadowrpcfly.client.remotetools.RemoteUploadTools;
import com.liubs.shadowrpcfly.config.ClientConfig;
import com.liubs.shadowrpcfly.listener.ShadowMessageListeners;
import com.liubs.shadowrpcfly.logging.Logger;
import org.junit.Test;
import rpctest.entity.MyMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liubsyy
 * @date 2023/12/18
 **/

public class HelloClient {

    private static final Logger logger = Logger.getLogger(HelloClient.class);


    /**
     * 调用hello方法
     */
    @Test
    public void helloClient() {
        ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
        shadowClient.init();


        IHello helloService = shadowClient.createRemoteProxy(IHello.class,"helloservice");

        logger.info("发送 hello 消息");
        String helloResponse = helloService.hello("Tom");
        logger.info("hello 服务端响应:"+helloResponse);

        MyMessage message = new MyMessage();
        message.setNum(100);
        message.setContent("Hello, Server!");

        System.out.printf("发送请求 : %s\n",message);
        MyMessage response = helloService.say(message);
        System.out.printf("接收服务端消息 : %s\n",response);
    }

    /**
     * 异步调用hello
     */
    @Test
    public void helloClientAsync() {
        ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
        shadowClient.init();


        IHello helloService = shadowClient.createRemoteProxy(IHello.class,"helloservice");

        ShadowClient.<String>asyncCall(()->{
            helloService.helloSlowly("Tom");
            logger.info("发送 hello 消息");
        },(helloResponse)->{
            logger.info("hello 服务端响应:"+helloResponse);
            System.exit(0);
        });

        MyMessage message = new MyMessage();
        message.setNum(100);
        message.setContent("Hello, Server!");

        ShadowClient.<MyMessage>asyncCall(()->{
            helloService.say(message);
            logger.info("message消息发送");
        },(messageResponse)->{
            logger.info("message消息响应:"+messageResponse);
        });

        logger.info("发消息工作已完毕");
        shadowClient.keep();
    }


    /**
     * 监听服务器消息
     */
    @Test
    public void listenMessage() {
        ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
        shadowClient.init();


        ShadowMessageListeners.getInstance().<MyMessage>addListener(MyMessage.class,
                message-> logger.info("收到消息:"+message.getContent()));

        IHello helloService = shadowClient.createRemoteProxy(IHello.class,"helloservice");
        helloService.helloForBroadcast("请求广播");

        //向服务器发送消息
        MyMessage message = new MyMessage();
        message.setContent("请求支援...");
        shadowClient.sendMessage(message);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 并发调用，测试拆包和粘包的可靠性
     */
    @Test
    public void helloConcurrent() throws InterruptedException {

        //每秒请求量统计
        AtomicInteger perSecondsRequests = new AtomicInteger(0);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            int qps = perSecondsRequests.getAndSet(0);
            if(qps > 0) {
                logger.info("Current Request: " + qps);
            }
        }, 0, 1, TimeUnit.SECONDS);

        ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
        shadowClient.init();

        //调用远程RPC接口
        IHello helloService = shadowClient.createRemoteProxy(IHello.class,"helloservice");

        System.out.println("发送 hello 消息");
        String helloResponse = helloService.hello("Tom");
        System.out.println("hello 服务端响应:"+helloResponse);

        List<Callable<String>> futureTaskList = new ArrayList<>();

        //预热
        ExecutorService executorService = new ThreadPoolExecutor(100, 100,
                3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for(int i = 0;i<100000;i++) {
            final int j = i;
            futureTaskList.add(()->{
                MyMessage message = new MyMessage();
                message.setNum(j);
                message.setContent(String.valueOf(j));
                helloService.say(message);
                return "success";
            });
        }
        executorService.invokeAll(futureTaskList);
        futureTaskList.clear();


        //正式并发测试
        final int n = 1000000;
        for(int i = 1;i<=n;i++) {
            final int j = i;
            futureTaskList.add(() -> {
                MyMessage message = new MyMessage();
                message.setNum(j);
                message.setContent("Hello, Server!");

                //打印消息影响速度，去掉打印至少快一倍
                //System.out.printf("发送请求%d \n",j);
                perSecondsRequests.incrementAndGet();
                MyMessage response = helloService.say(message);
                //System.out.printf("接收服务端消息%d \n",j);

                return "success";
            });
        }


        //Mac 2.2 GHz 四核Intel Core i7 内存 16G
        //100w个请求，25.8s 平均QPS=3.87w 最大qps=4.96w
        long time1 = System.currentTimeMillis();
        executorService.invokeAll(futureTaskList);
        long time2 = System.currentTimeMillis();
        long useMs = (time2-time1);
        logger.info("总共用时: "+useMs+" ms");

        executorService.shutdownNow();
        shadowClient.close();
    }

    //测试心跳
    @Test
    public void testHeartBeat() throws InterruptedException {
        ClientConfig config = new ClientConfig();
        config.setHeartBeat(true);
        config.setHeartBeatWaitSeconds(3);

        ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
        shadowClient.setConfig(config);
        shadowClient.init();
        while(true){
            Thread.sleep(1000);
        }
    }

    /**
     * 上传文件测试
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void uploadFileTest() throws IOException, InterruptedException {
        ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
        shadowClient.init();

        String name = "ideaIC-2019.3.5.dmg";
        RemoteUploadTools.uploadService(shadowClient,
                "/Users/liubs/Downloads/"+name,
                "target/upload/"+name, 10);

        Thread.sleep(10000);
    }

}
