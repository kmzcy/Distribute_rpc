package org.rpcframwork.core.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.loadbalance.LoadBalance;
import org.rpcframwork.core.loadbalance.loadbalancer.ConsistentHashLoadBalance;
import org.rpcframwork.core.registry.ServiceDiscovery;
import org.rpcframwork.utils.Factory.SingletonFactory;
import org.rpcframwork.utils.enums.RpcErrorMessageEnum;
import org.rpcframwork.utils.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscoveryImp implements ServiceDiscovery {
    public static CuratorFramework zkClient; // 用于连接zookeeper的客户端
    private final LoadBalance loadBalance;
    public ZkServiceDiscoveryImp(){
        zkClient = CuratorUtils.getZkClient();
        loadBalance = SingletonFactory.getInstance(ConsistentHashLoadBalance.class);
    }
    @Override
    public InetSocketAddress lookupService(RpcRequestBody rpcRequestBody) {
        List<String> serviceList = CuratorUtils.getChildrenNodes(zkClient, rpcRequestBody.getRpcServiceName());
        if(serviceList.isEmpty()){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcRequestBody.getRpcServiceName());
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceList, rpcRequestBody);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
