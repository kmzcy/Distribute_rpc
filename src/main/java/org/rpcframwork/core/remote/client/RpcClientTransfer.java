package org.rpcframwork.core.remote.client;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;

import java.net.InetSocketAddress;


public interface RpcClientTransfer {
    /**
     * 将RPC服务发送到服务器并取得结果
     * @param rpcRequest
     * @return
     */

    RpcResponse sendRequest(RpcRequest rpcRequest, InetSocketAddress inetSocketAddress);
}
