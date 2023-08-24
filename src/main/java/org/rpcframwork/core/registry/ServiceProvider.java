package org.rpcframwork.core.registry;

import org.rpcframwork.core.rpc_protocol.ServiceStatement;

public interface ServiceProvider {

    /**
     * @param serviceStatement 传入记录RPC服务状态的类
     */
    void addService(ServiceStatement serviceStatement);

    /**
     * @param rpcServiceName 传入RPC服务的名字
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param serviceStatement 传入记录RPC服务状态的类
     */
    void publishService(ServiceStatement serviceStatement);

}