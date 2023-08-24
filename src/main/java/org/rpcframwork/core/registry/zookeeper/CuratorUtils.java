package org.rpcframwork.core.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.rpcframwork.utils.PropertiesFileUtil;
import org.rpcframwork.utils.enums.RpcConfigEnum;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtils {
    private static final int BASE_SLEEP_TIME = 1000; // 用于在创建客户端时的重试策略，在连接不成功时进行连接重试
    private static final int MAX_RETRIES = 3; // 重试的策略
    public static final String ZK_REGISTER_ROOT_PATH = "/distribute_rpc"; // 在zookeeper中的根节点

    // 提供不同服务的服务器列表，相当于一个cache，当我们查询一个新服务后，会将其（key）和提供这个服务的服务器列表()value存入这个map中
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet(); // 已经注册过的服务的列表
    private static CuratorFramework zkClient; // 用于连接zookeeper的客户端
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "192.168.137.128:2181"; // zookeeper的地址


    private CuratorUtils() {}

    /**
     * 创建永久的节点
     * @param zkClient
     * @param path
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path){
        try {
            // eg: /distribute_rpc/InterfaceName.version.group/127.0.0.1:9999
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    public static void createEphemeralNode(CuratorFramework zkClient, String path){
        try {
            // eg: /distribute_rpc/InterfaceName.version.group/127.0.0.1:9999
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    /**
     * 获取一个节点下的所有子节点，用于发现服务
     *
     * @param rpcServiceName rpc service name eg:/distribute_rpc/InterfaceName.version.group
     * @return All child nodes under the specified node
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        // 如果这个子节点的服务之前查询过,直接从cache中获取
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            // 为这个节点的子节点注册watcher
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    /**
     * 为某个节点的子节点注册watcher
     * @param rpcServiceName
     * @param zkClient
     * @throws Exception
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    /**
     * 通过 CuratorFramework创建 zkClient
     * @param zkClient
     * @param inetSocketAddress
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress){
        System.out.println(inetSocketAddress.toString());
        REGISTERED_PATH_SET.stream().parallel().forEach(path -> {
            try {
                if(path.endsWith(inetSocketAddress.toString())){
                    zkClient.delete().forPath(path);
                }
            }catch (Exception e){
                log.info("clear registry for path [{}] fail", path);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }

    public static CuratorFramework getZkClient(){
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress = null;
        if(properties != null ){
            if((zookeeperAddress = properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue())) == null){
                zookeeperAddress = DEFAULT_ZOOKEEPER_ADDRESS;
            }
        }else {
            zookeeperAddress = DEFAULT_ZOOKEEPER_ADDRESS;
        }
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED){
            return zkClient;
        }
        System.out.println("zookeeperAddress: " + zookeeperAddress);

        // 设置重启策略ExponentialBackoffRetry是SleepingRetry的一个子类，基本的实际上就是暂停一段时间后（sleep）进行重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        
        zkClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    public static void main(String[] args){
        CuratorFramework zkClient = CuratorUtils.getZkClient();

    }
}
