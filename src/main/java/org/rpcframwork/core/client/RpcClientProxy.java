package org.rpcframwork.core.client;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.codec.RpcResponseBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 通过动态代理返回返回clazz的代理类，名字为RPC客户端代理
public class RpcClientProxy implements InvocationHandler {
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 1、将调用所需信息编码成bytes[]，即有了调用编码【codec层】
        // .builder() 它其实是一种设计模式，叫做建造者模式，它的含义是将一个复杂的对象的构建与它的表示分离，同样的构建过程可以创建不同的表示
        // 这里使用了lombok进行了优化
        RpcRequestBody rpcRequestBody = RpcRequestBody.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        // ByteArrayOutputStream 对byte类型数据进行写入的类 相当于一个中间缓冲层，将类写入到文件等其他outputStream。它是对字节进行操作，属于内存操作流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ObjectOutputStream(OutputStream out) 创建写入指定 OutputStream 的ObjectOutputStream。
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        // void writeObject(Object obj) 将指定的对象写入 ObjectOutputStream。
        oos.writeObject(rpcRequestBody);
        byte[] bytes = baos.toByteArray();

        // 2、创建RPC协议，将Header、Body的内容设置好（Body中存放调用编码）【protocol层】
        RpcRequest rpcRequest = RpcRequest.builder()
                .header("version=1")
                .body(bytes)
                .build();

        // 3、发送RpcRequest，获得RpcResponse 【网络传输层】
        RpcClientTransfer rpcClient = new RpcClientTransfer();
        RpcResponse rpcResponse = rpcClient.sendRequest(rpcRequest);

        // 4、解析RpcResponse，也就是在解析rpc协议【protocol层】
        String header = rpcResponse.getHeader();
        byte[] body = rpcResponse.getBody();

        if (header.equals("version=1")) {
            // 将RpcResponse的body中的返回编码，解码成我们需要的对象Object并返回【codec层】
            // 拿到结果，反序列化成我们所需要的对象
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            ObjectInputStream ois = new ObjectInputStream(bais);
            RpcResponseBody rpcResponseBody = (RpcResponseBody) ois.readObject();
            Object retObject = rpcResponseBody.getRetObject();
            return retObject;
        }
        return null;
    }
}
