package org.rpcframwork.core.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.rpcframwork.core.registry.ServiceList;
import org.rpcframwork.core.registry.ServiceRegistry;

@Slf4j
public class ServiceRegistryImp implements ServiceRegistry {
    private static CuratorFramework zkClient; // 用于连接zookeeper的客户端
    private boolean isAlive;
    public ServiceRegistryImp(){
        zkClient = CuratorUtils.getZkClient();
        isAlive = true;
    }
    @Override
    public void registerService(ServiceList serviceList) {
        for (String key: serviceList.keySet()){
            CuratorUtils.createPersistentNode(zkClient, key);
        }
        for (String key: serviceList.keySet()){
            CuratorUtils.createEphemeralNode(zkClient, key + serviceList.get(key));
        }
        SessionConnectionListener sessionConnectionListener = new SessionConnectionListener(serviceList);
        zkClient.getConnectionStateListenable().addListener(sessionConnectionListener);
    }

    private class SessionConnectionListener implements ConnectionStateListener {
        ServiceList serviceList;

        private SessionConnectionListener(ServiceList serviceList){
            this.serviceList = serviceList;
        }
        @Override
        public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState){
            if(connectionState == ConnectionState.LOST){
                log.error("zk session 超时");
                while (true){
                    try{
                        if(curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut() && isAlive){
                            for (String key: serviceList.keySet()){
                                CuratorUtils.createPersistentNode(zkClient, key);
                            }
                            for (String key: serviceList.keySet()){
                                CuratorUtils.createEphemeralNode(zkClient, key + serviceList.get(key));
                            }
                            log.info("[负载均衡修复]重连zk成功");
                        }
                        if(!isAlive) break;
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }


}
