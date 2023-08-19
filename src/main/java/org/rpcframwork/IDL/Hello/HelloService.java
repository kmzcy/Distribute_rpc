package org.rpcframwork.IDL.Hello;

// 方法传入的对象
public interface HelloService {
    HelloResponse hello(HelloRequest request);
    HelloResponse hi(HelloRequest request);
}
