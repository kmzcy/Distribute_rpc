package org.rpcframwork.core.loadbalance;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.rpc_protocol.RpcRequest;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequestBody rpcRequestBody) {
        if (serviceAddresses.isEmpty()) {
            return null;
        }
//        if (serviceAddresses.size() == 1) {
//            return serviceAddresses.get(0);
//        }
        return doSelect(serviceAddresses, rpcRequestBody);
    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequestBody rpcRequestBody);

}