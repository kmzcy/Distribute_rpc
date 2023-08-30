package org.rpcframwork.core.remote.client;

import org.rpcframwork.IDL.Hello.HelloRequest;
import org.rpcframwork.IDL.Hello.HelloResponse;
import org.rpcframwork.IDL.Hello.HelloService;
import org.rpcframwork.core.spring.annotation.RpcReference;
import org.springframework.stereotype.Component;

@Component
public class HelloServiceController {

    @RpcReference(version = "version1", group = "group1")
    HelloService helloService;

    public HelloResponse hello(HelloRequest request) {
        return helloService.hello(request);
    }
    public HelloResponse hi(HelloRequest request){
        return helloService.hi(request);
    }

}