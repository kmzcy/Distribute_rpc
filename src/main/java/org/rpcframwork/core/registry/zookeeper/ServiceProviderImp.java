package org.rpcframwork.core.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.rpcframwork.core.registry.ServiceProvider;

public class ServiceProviderImp implements ServiceProvider {
    public static CuratorFramework zkClient; // 用于连接zookeeper的客户端
    public ServiceProviderImp(){
        zkClient = CuratorUtils.getZkClient();
    }

    /**
     *
     * @param rpcServiceName full name org.rpcframwork.IDL.Hello.HelloServicegroup1version1/127.0.0.1:9999
     * @return
     */
    @Override
    public byte[] getService(String rpcServiceName) {
        return CuratorUtils.getNodesContent(zkClient, rpcServiceName);
    }
}
