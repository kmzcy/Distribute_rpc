package org.rpcframwork.core.remote.handler;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.codec.RpcResponseBody;
import org.rpcframwork.core.registry.RegisterCenter;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.utils.Factory.SingletonFactory;
import org.rpcframwork.utils.enums.RpcErrorMessageEnum;
import org.rpcframwork.utils.exception.RpcException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  通过反射的方式处理所有的RPC请求
 */
public class ServerRequestHandler {
    private final RegisterCenter registerCenter;
    private final KryoSerializer kryoSerializer;
    public ServerRequestHandler(){
        // 需要提供：注册了什么注册中心，使用了什么样的序列化
        registerCenter = SingletonFactory.getInstance(RegisterCenter.class);
        // 序列化应改成接口
        kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);
    }

    /**
     * 处理RPC请求
     * @param rpcRequest
     * @return RpcResponse
     */
    public RpcResponse handle(RpcRequest rpcRequest){
        RpcRequestBody rpcRequestBody = protocolDecode(rpcRequest);
        Object service = registerCenter.getService(rpcRequestBody.getInterfaceName());
        Object result = invokeTargetMethod(rpcRequestBody, service);
        return protocolEncode(result);
    }

    /**
     * 用于解析 Rpc协议获得序列化的对象
     * @param rpcRequest
     * @return rpcRequestBody
     */
    public RpcRequestBody protocolDecode(RpcRequest rpcRequest){
        // 服务器端信息核对
        if (!rpcRequest.getHeader().equals("version=1")){
            throw new RpcException(RpcErrorMessageEnum.PROTOCOL_VERSION_NOT_MATCH);
        }
        // 将rpcRequest中的body部分解码出来变成RpcRequestBody【codec层】
        byte[] body = rpcRequest.getBody();
        // RpcRequestBody对象反序列化
        RpcRequestBody rpcRequestBody = kryoSerializer.deserialize(body, RpcRequestBody.class) ;

        return rpcRequestBody;
    }

    /**
     * 用于将返回的对象放入RPC协议之中
     * @param o
     * @return RPC协议传回对象
     */
    public RpcResponse protocolEncode(Object o){
        // 1、将returnObject编码成bytes[]即变成了返回编码【codec层】
        RpcResponseBody rpcResponseBody = RpcResponseBody.builder()
                .retObject(o)
                .build();
        // RpcResponseBody对象序列化
        byte[] bytes = kryoSerializer.serialize(rpcResponseBody);

        // 2、将返回编码作为body，加上header，生成RpcResponse协议【protocol层】
        RpcResponse rpcResponse = RpcResponse.builder()
                .header("version=1")
                .body(bytes)
                .build();

        return rpcResponse;
    }

    /**
     * 传入请求的服务的对象和注册中心中获得的提供服务的对象，通过反射的方式调用服务
     * @param rpcRequestBody
     * @param service
     * @return 调用方法的结果
     */
    private Object invokeTargetMethod(RpcRequestBody rpcRequestBody, Object service) {
        Object returnObject;
        try{
            // invoke反射
            Method method = service.getClass().getMethod(rpcRequestBody.getMethodName(), rpcRequestBody.getParamTypes());
            returnObject = method.invoke(service, rpcRequestBody.getParameters());
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            throw new RpcException(e.getMessage(), e);
        }
       return returnObject;
    }
}
