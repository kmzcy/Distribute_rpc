package org.rpcframwork.core.registry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * 注册中心需要提供实现注册服务的接口的功能
 */
public interface ServiceRegistry {
    /**
     * 注册服务，服务的标识为：/distribute_rpc/org.rpcframwork.IDL.Hello.HelloServicegroup1version1
     *      *               rpcServiceName 对应 ServiceStatement 中的 getRpcServiceName()
     *
     * @param serviceList  rpc service list(HashMap<String, InetSocketAddress>)
     */
    void registerService(ServiceList serviceList, Map<String, byte[]> serviceStatementList);

    /**
     * 确认连接状态，true 连接，false 断开
     * @return
     */
    boolean isConnected();

    /**
     * 关闭连接
     */
    void linkClose();

}
