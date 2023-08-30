package client;

import org.rpcframwork.IDL.Hello.HelloRequest;
import org.rpcframwork.IDL.Hello.HelloResponse;

import org.rpcframwork.core.remote.client.HelloServiceController;
import org.rpcframwork.core.spring.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@RpcScan(packageToScan = {"client"})
public class TestClient {
    public static void main(String[] args) {
        // 获取HelloService
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestClient.class);
        HelloServiceController helloServiceController = applicationContext.getBean(HelloServiceController.class);
        HelloRequest helloRequest = new HelloRequest("from tesClient");

        // 调用hellow 方法
        HelloResponse helloResponse = helloServiceController.hello(helloRequest);
        String helloMsg = helloResponse.getMsg();
        System.out.println(helloMsg);

        // 调用hi方法
        HelloResponse hiResponse = helloServiceController.hi(helloRequest);
        String hiMsg = hiResponse.getMsg();
        System.out.println(hiMsg);
    }
}
