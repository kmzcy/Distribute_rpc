package org.rpcframwork.core.server;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.codec.RpcResponseBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;

public class RpcServerWorker implements Runnable{ //继承runnable，作为一个线程

    private Socket socket;
    private HashMap<String, Object> registeredService;

    public RpcServerWorker(Socket socket, HashMap<String, Object> registeredService) {
        this.socket = socket;
        this.registeredService = registeredService;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // 1、Transfer层获取到RpcRequest消息【transfer层】
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();

            // 2、解析版本号，并判断【protocol层】
            if (rpcRequest.getHeader().equals("version=1")) {

                // 3、将rpcRequest中的body部分解码出来变成RpcRequestBody【codec层】
                byte[] body = rpcRequest.getBody();
                // RpcRequestBody对象反序列化
                KryoSerializer kryoSerializer = new KryoSerializer();
                RpcRequestBody rpcRequestBody = kryoSerializer.deserialize(body, RpcRequestBody.class) ;

                // 调用服务
                Object service = registeredService.get(rpcRequestBody.getInterfaceName());
                // invoke反射
                Method method = service.getClass().getMethod(rpcRequestBody.getMethodName(), rpcRequestBody.getParamTypes());
                Object returnObject = method.invoke(service, rpcRequestBody.getParameters());

                // 1、将returnObject编码成bytes[]即变成了返回编码【codec层】
                RpcResponseBody rpcResponseBody = RpcResponseBody.builder()
                        .retObject(returnObject)
                        .build();
                // RpcResponseBody对象序列化
                byte[] bytes = kryoSerializer.serialize(rpcResponseBody);

                // 2、将返回编码作为body，加上header，生成RpcResponse协议【protocol层】
                RpcResponse rpcResponse = RpcResponse.builder()
                        .header("version=1")
                        .body(bytes)
                        .build();
                // 3、发送【transfer层】
                objectOutputStream.writeObject(rpcResponse);
                objectOutputStream.flush();
            }

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
