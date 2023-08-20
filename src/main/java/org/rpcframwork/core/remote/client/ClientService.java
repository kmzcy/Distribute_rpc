package org.rpcframwork.core.remote.client;

import org.rpcframwork.core.remote.client.socket.SocketRpcClientProxy;
import org.rpcframwork.core.remote.client.socket.SocketRpcClientTransfer;

public class ClientService {
    public <T> T getService(Class<T> service){
        RpcClientTransfer RpcClientTransfer = new SocketRpcClientTransfer();
        RpcClientProxy proxy = new SocketRpcClientProxy(RpcClientTransfer);
        return proxy.getService(service);
    }
}