package org.rpcframwork.core.remote.server;

import org.rpcframwork.core.codec.ServiceStatement;

import java.util.List;

/**
 * 服务器的公共接口，包含两个方法
 *
 * @author kmzcy
 */
public interface RpcServer {
    /**
     * 将服务器提供的方法注册到注册中心
     * @param serviceList
     */
    void register(List<ServiceStatement> serviceList);

    /**
     * 启动服务器
     */
    void start();
}
