package org.rpcframwork.core.client;

public class ClientService {
    public <T> T getService(Class<T> service){
        RpcClientTransfer rpcClient = new RpcClientTransfer();
        RpcClientProxy proxy = new RpcClientProxy(rpcClient);
        return proxy.getService(service);
    }
}