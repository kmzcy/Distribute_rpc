package org.rpcframwork.core.remote.handler;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.utils.Factory.SingletonFactory;

import java.lang.reflect.Method;

public class RequestSendHandler {
    private final KryoSerializer kryoSerializer;

    public RequestSendHandler(){
        kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);
    }

    public RpcRequest handler(RpcRequestBody requestBody){
        byte[] bytes = Serializer(requestBody);
        return BuildRpcRequest(bytes);
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
