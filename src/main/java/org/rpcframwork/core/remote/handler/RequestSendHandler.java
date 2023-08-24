package org.rpcframwork.core.remote.handler;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.utils.Factory.SingletonFactory;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

class RequestIdProvider{
    static Integer requestId = new Random(100).nextInt(0,100);
    static String getRequestId(){
        String Id = "";
        try{
            Id = Id + InetAddress.getLocalHost().getHostAddress().replaceAll("\\.","");
        }catch (UnknownHostException e){
            System.out.println("can not get LocalHost");
            e.printStackTrace();
            System.exit(1);
        }

        synchronized(requestId){
            Id = Id + requestId.intValue();
            requestId++;
        }
        return Id;
    }
}

public class RequestSendHandler {
    private final KryoSerializer kryoSerializer;

    public RequestSendHandler(){
        kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);
    }

    public RpcRequest handler(Method method, Object[] args){
        RpcRequestBody requestBody = BuildRequestBody(method, args, RequestIdProvider.getRequestId());
        byte[] bytes = Serializer(requestBody);
        return BuildRpcRequest(bytes);
    }


    public RpcRequestBody BuildRequestBody(Method method, Object[] args, String requestId){
        // 构建请求体
        // .builder() 它其实是一种设计模式，叫做建造者模式，它的含义是将一个复杂的对象的构建与它的表示分离，同样的构建过程可以创建不同的表示
        // 这里使用了lombok进行了优化
        // 创建管理器，管理requestId
        return RpcRequestBody.builder()
                .requestId(requestId)
                .version("version1")
                .group("group1")
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .build();
    }

    public byte[] Serializer(RpcRequestBody rpcRequestBody){
        return kryoSerializer.serialize(rpcRequestBody);
    }

    public RpcRequest BuildRpcRequest(byte[] bytes){
        return RpcRequest.builder()
                .header("version=1")
                .body(bytes)
                .build();
    }
}
