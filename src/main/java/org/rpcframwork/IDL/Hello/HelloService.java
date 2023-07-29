package org.rpcframwork.IDL.Hello;

public interface HelloService {
    HelloResponse hello(HelloRequest request);
    HelloResponse hi(HelloRequest request);
}
