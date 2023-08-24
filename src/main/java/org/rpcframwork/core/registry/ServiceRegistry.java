package org.rpcframwork.core.registry;

import java.net.InetSocketAddress;

/**
 * 注册中心需要提供实现注册服务的接口的功能
 */
public interface ServiceRegistry {
    /**
     * 注册服务，服务的标识为：InterfaceName.version.group 以 key 的形式存储在 ServiceList中
     * value为服务的 ip地址
     *
     * @param serviceList  rpc service list
     *
     */
    void registerService(ServiceList serviceList);
}
