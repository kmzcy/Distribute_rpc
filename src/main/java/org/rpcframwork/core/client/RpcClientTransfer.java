package org.rpcframwork.core.client;

import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// 传入protocol层的RpcRequest，输出protocol层的RpcResponse
// 实际就是走socket层发包
public class RpcClientTransfer {

    public RpcResponse sendRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket("localhost", 9000)) {
            // 发送【transfer层】
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();

            // 等待responce
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();

            return rpcResponse;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
