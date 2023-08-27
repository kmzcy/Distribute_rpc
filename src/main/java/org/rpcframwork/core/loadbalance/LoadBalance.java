package org.rpcframwork.core.loadbalance;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;

import java.util.List;

public interface LoadBalance {

    /**
     * 从服务器列表中选择一个需要的服务器，来请求对应的服务
     *
     * @param serviceUrlList Service address list
     * @param rpcRequestBody
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequestBody rpcRequestBody);
}
