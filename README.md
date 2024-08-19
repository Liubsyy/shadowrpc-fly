# shadowrpc-fly

基于netty点到点全双工通信的高性能精简rpc框架，由[ShadowRPC](https://github.com/Liubsyy/ShadowRPC) 精简而来。

本rpc框架只做client-server点到点通信，移除了ShadowRPC的集群模式和protobuf序列化等模块，只保留精简的netty+kryo。

## 主要模块
- client : 客户端，基于netty+kryo
- client-all : 客户端全依赖包，仅1.8M
- server : 服务端，基于netty+kryo


## 功能
- rpc服务调用：支持同步和异步调用
- 全双工通信：服务端可主动向客户端发送消息，客户端也可向服务器发送消息

## 性能
以下压测数据来自100w个请求的数据统计
配置|耗时|平均QPS|瞬时最高QPS
---|---|---|---
MacBook 2.2 GHz 4核 Intel Core i7 内存 16G |23.8s|4.2w|5.4w
MacBook M1芯片 8核心 内存16G |12.4s|8w|10w
win10笔记本 i7 4核 2.8GHz 内存|25.6s|3.9w|4.1w


## 快速使用

> 以下例子在client和server模块中都有单侧

先根据需要引用maven依赖: 

```xml
<!-- shadowrpcfly client 依赖-->
<dependency>
    <groupId>io.github.liubsyy</groupId>
    <artifactId>shadowrpcfly-client</artifactId>
    <version>1.0.2</version>
</dependency>
```

```xml
<!-- shadowrpcfly client 全依赖包-->
<dependency>
    <groupId>io.github.liubsyy</groupId>
    <artifactId>shadowrpcfly-client-all</artifactId>
    <version>1.0.2</version>
</dependency>
```

```xml
<!-- shadowrpcfly server 依赖-->
<dependency>
    <groupId>io.github.liubsyy</groupId>
    <artifactId>shadowrpcfly-server</artifactId>
    <version>1.0.2</version>
</dependency>
```


### 1. 服务的定义

#### 1.1 定义实体

可定义实体作为服务函数参数和返回类型，如服务函数为简单函数则不需要定义实体

`@ShadowEntity`定义实体类

`@ShadowField`标识字段，当服务端字段增加或者删除时客户端可兼容，反之亦然，但是不支持不同类型兼容。

```java
@ShadowEntity
public class MyMessage {
    @ShadowField(1)
    private String content;

    @ShadowField(2)
    private int num;
    
    //getter,setter...
}
```

#### 1.2 编写接口

写一个interface作为client-server通信的接口，加上`@ShadowInterface`注解

```java
@ShadowInterface
public interface IHello {
    String hello(String msg);
    String helloSlowly(String msg);
    MyMessage say(MyMessage message);
}
```

#### 1.3 编写服务实现类

编写服务实现类，加上`@ShadowService`注解，serviceName标注服务名

```java
@ShadowService(serviceName = "helloservice")
public class HelloService implements IHello {
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
        return "Slowly Hello,"+msg;
    }

    @Override
    public MyMessage say(MyMessage message) {
        MyMessage message1 = new MyMessage();
        message1.setContent("hello received "+"("+message.getContent()+")");
        message1.setNum(message.getNum()+1);
        return message1;
    }
}
```

#### 1.4 启动server

先设置端口
```java
ServerConfig serverConfig = new ServerConfig();
serverConfig.setPort(2023);
```

扫描服务所在包(addPackage)，再启动具体服务
```java
Server server = ServerBuilder.newBuilder().serverConfig(serverConfig).addPackage("rpctest.hello")
                .build().start();
```


### 2. 服务调用

#### 先连接服务器
```java
ShadowClient shadowClient = new ShadowClient("127.0.0.1",2023);
shadowClient.init();
```

#### 调用远程服务
```java
IHello helloService = shadowClient.createRemoteProxy(IHello.class,"helloservice");
String helloResponse = helloService.hello("Tom");
```

远程调用`@ShadowEntity`作为接口参数/响应的服务
```java
MyMessage message = new MyMessage();
message.setNum(100);
message.setContent("Hello, Server!");

MyMessage response = helloService.say(message);
```

#### 异步调用接口
使用 ShadowClient.asyncCall(AsyncCall asyncCall, Consumer<T> callBack) 异步调用远程服务
asyncCall中写调用远程逻辑，callBack中写回调函数

```java
ShadowClient.<String>asyncCall(
                ()-> helloService.helloSlowly("Tom"),
                (helloResponse)-> logger.info("hello 服务端响应:"+helloResponse) );
```

### 3. 全双工消息通信

服务端可主动向客户端发送消息和广播消息，客户端也可向服务器发送消息。

可用于一次请求异步响应或多次响应的场景，也可适用与服务器主动通知客户端的场景。

#### 服务器发消息

广播消息
```java
Server.broadcastMessage(msg);
```

向某个客户端发消息
```java
Server.sendMessage(ChannelHandlerContext ctx, Object msg);
```

在服务函数内或者监听消息环境内发送消息，目标是当前持有环境上下文的客户端
```java
Server.sendMessage(Object msg);
```

#### 客户端发消息

客户端向服务器发消息，目标为shadowClient连接的服务器
```java
shadowClient.sendMessage(message);
```

#### 消息监听
启动时注册消息监听器（响应函数），一个Class对于的消息可注册多个监听者（响应函数）

```java
ShadowMessageListeners.getInstance().<T>addListener
        (final Class<?> message, IShadowMessageListener<T> listener);
```

例子：
```java
ShadowMessageListeners.getInstance().<MyMessage>addListener(MyMessage.class,
        message-> logger.info("收到消息:"+message.getContent()));
```




