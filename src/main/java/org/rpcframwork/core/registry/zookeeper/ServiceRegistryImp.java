package org.rpcframwork.core.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.CloseableUtils;
import org.rpcframwork.core.registry.ServiceList;
import org.rpcframwork.core.registry.ServiceRegistry;

import java.util.Map;

@Slf4j
public class ServiceRegistryImp implements ServiceRegistry {
    public static CuratorFramework zkClient; // 用于连接zookeeper的客户端
    public ServiceRegistryImp(){
        zkClient = CuratorUtils.getZkClient();
    }
    @Override
    public void registerService(ServiceList serviceList, Map<String, byte[]> serviceStatementList) {
        // org.rpcframwork.IDL.Hello.HelloServicegroup1version1/192.168.137.3:9000
        // key: HelloServicegroup1version1
        // value: 192.168.137.3:9000

        for (String key: serviceList.keySet()){
            CuratorUtils.createPersistentNode(zkClient, key);
        }
        for (String key: serviceList.keySet()){
            CuratorUtils.createEphemeralNode(zkClient, key + serviceList.get(key), serviceStatementList.get(key));
        }
        SessionConnectionListener sessionConnectionListener = new SessionConnectionListener(serviceList, serviceStatementList);
        zkClient.getConnectionStateListenable().addListener(sessionConnectionListener);
    }

    private class SessionConnectionListener implements ConnectionStateListener {
        ServiceList serviceList;
        Map<String, byte[]> serviceStatementList;
        private SessionConnectionListener(ServiceList serviceList, Map<String, byte[]> serviceStatementList){
            this.serviceList = serviceList;
            this.serviceStatementList = serviceStatementList;
        }
        @Override
        public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState){
            if(connectionState == ConnectionState.LOST){
                log.error("zk session 超时");
                while (true){
                    try{
                        if(curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()){
                            for (String key: serviceList.keySet()){
                                CuratorUtils.createPersistentNode(zkClient, key);
                            }
                            for (String key: serviceList.keySet()){
                                CuratorUtils.createEphemeralNode(zkClient, key + serviceList.get(key), serviceStatementList.get(key));
                            }
                            log.info("[负载均衡修复]重连zk成功");
                            break;
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
            if(connectionState == ConnectionState.SUSPENDED){
                System.out.println("in SessionConnectionListener ConnectionState.SUSPENDED");
            }

        }
    }


    public boolean isConnected(){
        return zkClient.getZookeeperClient().isConnected();
    }

    public void linkClose(){
        CloseableUtils.closeQuietly(zkClient);
    }


}
