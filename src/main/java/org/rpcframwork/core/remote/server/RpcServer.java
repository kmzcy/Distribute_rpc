package org.rpcframwork.core.remote.server;

import org.rpcframwork.core.rpc_protocol.ServiceStatement;

/**
 * 服务器的公共接口，包含两个方法
 *
 * @author kmzcy
 */
public interface RpcServer {
    /**
     * 将服务器提供的方法注册到注册中心
     * @param service
     */
    void register(ServiceStatement service);

    /**
     * 启动服务器
     */
    void start();
}
