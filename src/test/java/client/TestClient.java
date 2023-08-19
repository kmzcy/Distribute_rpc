package client;

import org.rpcframwork.IDL.Hello.HelloRequest;
import org.rpcframwork.IDL.Hello.HelloResponse;
import org.rpcframwork.IDL.Hello.HelloService;
import org.rpcframwork.core.client.ClientService;

public class TestClient {
    public static void main(String[] args) {
        // 获取HelloService
        ClientService clientService = new ClientService();
        HelloService helloService = clientService.getService(HelloService.class);
        HelloRequest helloRequest = new HelloRequest("from tesClient");

        // 调用hellow 方法
        HelloResponse helloResponse = helloService.hello(helloRequest);
        String helloMsg = helloResponse.getMsg();
        System.out.println(helloMsg);

        // 调用hi方法
        HelloResponse hiResponse = helloService.hi(helloRequest);
        String hiMsg = hiResponse.getMsg();
        System.out.println(hiMsg);

    }
}
