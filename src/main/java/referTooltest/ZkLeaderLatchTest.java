package referTooltest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;

public class ZkLeaderLatchTest {

    protected static String PATH = "/francis/leader";
    private final static String connectionInfo = "192.168.137.128:2181";
    private static final int CLIENT_QTY = 10;

    public static void main(String[] args) {
        List<CuratorFramework> clients = new ArrayList<>();
        List<LeaderLatch> examples = new ArrayList<>();

        try {

            /**
             * 创建10个客户端，并且创建对应的LeaderLatch
             */
            for (int i = 0; i < CLIENT_QTY; i++) {
                CuratorFramework client
                        = CuratorFrameworkFactory.newClient(connectionInfo, new ExponentialBackoffRetry(20000, 3));
                client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                    @Override
                    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                        System.out.println("Connection state changed: " + connectionState);
                    }
                });

                clients.add(client);
                LeaderLatch latch = new LeaderLatch(client, PATH, "Client #" + i);

                latch.addListener(new LeaderLatchListener(){
                    @Override
                    public void isLeader() {
                        System.out.println("I am Leader");
                    }

                    @Override
                    public void notLeader() {
                        System.out.println("I am not Leader");
                    }
                });

                examples.add(latch);
                client.start();
                latch.start();
            }

            Thread.sleep(10_000);
            /**
             * 获取当前的leader
             */
            LeaderLatch currentLeader = null;
            for (LeaderLatch latch : examples) {
                if (latch.hasLeadership()) {
                    currentLeader = latch;
                }
            }
            System.out.println("current leader is " + currentLeader.getId());
            System.out.println("release the leader " + currentLeader.getId());
            // 只能通过close释放当前的领导权
            currentLeader.close();

            Thread.sleep(5000);

            /**
             * 再次获取leader
             */
            for (LeaderLatch latch : examples) {
                if (latch.hasLeadership()) {
                    currentLeader = latch;
                }
            }
            System.out.println("current leader is " + currentLeader.getId());
            System.out.println("release the leader " + currentLeader.getId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (LeaderLatch latch : examples) {
                System.out.println(latch.getState());
                if (LeaderLatch.State.CLOSED != latch.getState())
                    CloseableUtils.closeQuietly(latch);


            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
