package server;

import org.rpcframwork.IDL.Hello.HelloRequest;
import org.rpcframwork.IDL.Hello.HelloResponse;
import org.rpcframwork.IDL.Hello.HelloService;
import org.rpcframwork.core.spring.annotation.RpcService;

@RpcService(version = "version1", group = "group1")
public class HelloServiceImpl implements HelloService {
    @Override
    public HelloResponse hello(HelloRequest request) {
        String name = request.getName();
        String retMsg = "hello: " + name;
        HelloResponse response = new HelloResponse(retMsg);
        return response;
    }

    @Override
    public HelloResponse hi(HelloRequest request) {
        String name = request.getName();
        String retMsg = "hi: " + name;
        HelloResponse response = new HelloResponse(retMsg);
        return response;
    }
}
