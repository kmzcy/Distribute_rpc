package org.rpcframwork.core.registry;

import org.rpcframwork.core.codec.RpcRequestBody;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 服务发现，用于从注册中心发现需要的服务并返回一个经过负载均衡算法筛选的服务器
 *
 */

public interface ServiceDiscovery {
    /**
     * lookup service by rpcServiceName
     *
     * @param rpcRequestBody rpc service 的请求体，从中可以知道请求的是什么方法
     * @return service address 提供服务的地址
     */
    InetSocketAddress lookupService(RpcRequestBody rpcRequestBody);

}