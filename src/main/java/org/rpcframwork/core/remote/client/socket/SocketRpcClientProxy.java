package org.rpcframwork.core.remote.client.socket;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.codec.RpcResponseBody;
import org.rpcframwork.core.remote.client.RpcClientProxy;
import org.rpcframwork.core.remote.client.RpcClientTransfer;
import org.rpcframwork.core.remote.handler.RequestGetHandler;
import org.rpcframwork.core.remote.handler.RequestSendHandler;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.utils.Factory.SingletonFactory;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;


// 通过动态代理返回返回clazz的代理类，名字为RPC客户端代理
public class SocketRpcClientProxy implements InvocationHandler, RpcClientProxy {
    private RpcClientTransfer rpcClientTransfer;
    private final RequestSendHandler requestSendHandler;
    private final RequestGetHandler requestGetHandler;

    public SocketRpcClientProxy(RpcClientTransfer rpcClientTransfer){
        this.rpcClientTransfer = rpcClientTransfer;
        requestSendHandler = SingletonFactory.getInstance(RequestSendHandler.class);
        requestGetHandler = SingletonFactory.getInstance(RequestGetHandler.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        // 如果没有实现类，在调用Proxy.newProxyInstance时，要注意传入的interfaces参数，
        // 没有实现类，则需要用new Class[]{ProxyInterface.class}的方法传入，这样就可以生成代理了，
        // 如果传的是new Class[0]，那么默认只会给toString方法代理
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){

        // 处理请求获取RpcRequest
        RpcRequest rpcRequest = requestSendHandler.handler(method, args);

        // 从注册中心获取相关服务的
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 9000);
        // 3、发送RpcRequest，获得RpcResponse 【网络传输层】
        RpcResponse rpcResponse = rpcClientTransfer.sendRequest(rpcRequest, inetSocketAddress);

        // 4、解析RpcResponse，也就是在解析rpc协议【protocol层】，传入method是为了和返回的response对象核对信息，看看是不是之前请求的对象
        Object retObject = requestGetHandler.handel(method, rpcResponse);
        return retObject;
    }

}
