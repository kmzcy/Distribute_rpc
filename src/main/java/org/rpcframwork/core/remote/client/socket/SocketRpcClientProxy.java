package org.rpcframwork.core.remote.client.socket;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.codec.RpcResponseBody;
import org.rpcframwork.core.remote.client.RpcClientProxy;
import org.rpcframwork.core.remote.client.RpcClientTransfer;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


// 通过动态代理返回返回clazz的代理类，名字为RPC客户端代理
public class SocketRpcClientProxy implements InvocationHandler, RpcClientProxy {
    RpcClientTransfer rpcClientTransfer;

    public SocketRpcClientProxy(RpcClientTransfer rpcClientTransfer){
        this.rpcClientTransfer = rpcClientTransfer;
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        // 如果没有实现类，在调用Proxy.newProxyInstance时，要注意传入的interfaces参数，
        // 没有实现类，则需要用new Class[]{ProxyInterface.class}的方法传入，这样就可以生成代理了，
        // 如果传的是new Class[0]，那么默认只会给toString方法代理
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 构建请求体
        // .builder() 它其实是一种设计模式，叫做建造者模式，它的含义是将一个复杂的对象的构建与它的表示分离，同样的构建过程可以创建不同的表示
        // 这里使用了lombok进行了优化
        RpcRequestBody rpcRequestBody = RpcRequestBody.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] bytes = kryoSerializer.serialize(rpcRequestBody);

        // 2、创建RPC协议，将Header、Body的内容设置好（Body中存放调用编码）【protocol层】
        RpcRequest rpcRequest = RpcRequest.builder()
                .header("version=1")
                .body(bytes)
                .build();

        // 3、发送RpcRequest，获得RpcResponse 【网络传输层】
        RpcResponse rpcResponse = rpcClientTransfer.sendRequest(rpcRequest);

        // 4、解析RpcResponse，也就是在解析rpc协议【protocol层】
        String header = rpcResponse.getHeader();
        byte[] body = rpcResponse.getBody();

        if (header.equals("version=1")) {
            // 将RpcResponse的body中的返回编码，解码成我们需要的对象Object并返回【codec层】
            // 拿到结果，反序列化成我们所需要的对象
            RpcResponseBody rpcResponseBody = kryoSerializer.deserialize(body, RpcResponseBody.class);
            // 拿出body对象中的Object
            Object retObject = rpcResponseBody.getRetObject();

            return retObject;
        }
        return null;
    }
}