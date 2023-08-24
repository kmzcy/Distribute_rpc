package org.rpcframwork.core.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

@Slf4j
public class ZookeeperTest {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static void main(String[] args){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("192.168.137.128:2181")
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();
        try{
            Watcher watcher = watchedEvent -> System.out.println("监听到的变化 watchedEvent = " + watchedEvent);

            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1");
            // zkClient.delete().forPath("/node1");
            byte[] content = zkClient.getData().usingWatcher(watcher).forPath("/node1");

            log.info("监听节点内容：" + new String(content));

            System.out.println("zkClient.checkExists().forPath(\"/node1\"): " + zkClient.checkExists().forPath("/node1"));

            zkClient.setData().forPath("/node1", "/node2".getBytes());

            System.out.println("zkClient.checkExists().forPath(\"/node1\"): " + zkClient.checkExists().forPath("/node1"));

        }catch (Exception e){
            System.out.println("catch");
            e.printStackTrace();
        }
    }
}
