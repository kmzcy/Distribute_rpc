package org.rpcframwork.core.remote.handler;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.codec.RpcResponseBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.utils.Factory.SingletonFactory;

import java.lang.reflect.Method;

public class RequestGetHandler {
    private final KryoSerializer kryoSerializer;
    public RequestGetHandler(){
        kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);
    }

    public Object handel(Method method, RpcResponse rpcResponse){
        // 解包
        // 校对
        // 返回
        String header = rpcResponse.getHeader();
        byte[] body = rpcResponse.getBody();

        if (!header.equals("version=1")) {
        }
        // 将RpcResponse的body中的返回编码，解码成我们需要的对象Object并返回【codec层】
        // 拿到结果，反序列化成我们所需要的对象
        RpcResponseBody rpcResponseBody = kryoSerializer.deserialize(body, RpcResponseBody.class);
        System.out.println("rpcResponseBody.getVersion(): " + rpcResponseBody.getVersion());
        System.out.println("rpcResponseBody.getGroup(): " + rpcResponseBody.getGroup());
        System.out.println("rpcResponseBody.getRequestId(): " + rpcResponseBody.getRequestId());
        System.out.println("rpcResponseBody.getInterfaceName(): " + rpcResponseBody.getInterfaceName());
        System.out.println("rpcResponseBody.getMethodName(): " + rpcResponseBody.getMethodName());
        System.out.println("rpcResponseBody.getMessage(): " + rpcResponseBody.getMessage());
        // 拿出body对象中的Object
        Object retObject = rpcResponseBody.getRetObject();
        return retObject;
    }

    public RpcRequestBody protocolDecode(RpcResponse rpcResponse){
        return null;
    }

}
