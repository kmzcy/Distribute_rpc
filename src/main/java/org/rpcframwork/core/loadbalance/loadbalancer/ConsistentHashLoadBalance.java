package org.rpcframwork.core.loadbalance.loadbalancer;

import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.loadbalance.AbstractLoadBalance;
import org.rpcframwork.core.rpc_protocol.RpcRequest;

import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<String, ConsistentHashSelector>();
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequestBody rpcRequestBody) {
        return super.selectServiceAddress(serviceAddresses, rpcRequestBody);
    }

    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequestBody rpcRequestBody) {
        String key = rpcRequestBody.getRpcServiceName();

        int identityHashCode = System.identityHashCode(serviceAddresses);
        ConsistentHashSelector selector = selectors.get(key);
        if(selector == null || selector.identityHashCode != identityHashCode){
            selectors.put(key, new ConsistentHashSelector(serviceAddresses, identityHashCode));
            selector = selectors.get(key);
        }
        return selector.select(rpcRequestBody);
    }

    private static final class ConsistentHashSelector{
        private final TreeMap<Long, String> virtualServerNode;
        private final int replicaNumber = 160;
        private final int identityHashCode;

        public ConsistentHashSelector(List<String> serviceAddresses, int identityHashCode){
            this.identityHashCode = identityHashCode;
            this.virtualServerNode = new TreeMap<>();
            for(String address : serviceAddresses){
                for(int i = 0; i < replicaNumber / 4; i++){
                    // 对 address + i 进行 md5 运算，得到一个长度为16的字节数组
                    byte[] digest = md5(address + i);
                    for (int h = 0; h < 4; h++) {
                        // h = 0 时，取 digest 中下标为 0 ~ 3 的4个字节进行位运算
                        // h = 1 时，取 digest 中下标为 4 ~ 7 的4个字节进行位运算
                        // h = 2, h = 3 时过程同上
                        long m = hash(digest, h);
                        // 将 hash 到 address 的映射关系存储到 virtualServerNode 中，
                        // virtualInvokers 需要提供高效的查询操作，因此选用 TreeMap 作为存储结构
                        virtualServerNode.put(m, address);
                    }
                }
            }

        }
        /**
         * MD5的作用是让大容量信息在用数字签名软件签署私人密钥前被"压缩"成一种保密的格式，
         * 就是把一个任意长度的字节串变换成一定长的16进制字节串。
         * @param key
         * @return
         */
        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        /**
         * digest: 16位 0xFF: 1111 1111
         * @param digest
         * @param number
         * @return
         */
        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public String select(RpcRequestBody rpcRequestBody) {
            // 将参数转为 key
            String key = rpcRequestBody.getRequestId();
            // 对参数 key 进行 md5 运算
            byte[] digest = md5(key);
            // 取 digest 数组的前四个字节进行 hash 运算，再将 hash 值传给 selectForKey 方法，
            // 寻找合适的 Invoker
            return selectForKey(hash(digest, 0));
        }


        private String selectForKey(long hash) {
            // 到 TreeMap 中查找第一个节点值大于或等于当前 hash 的 Invoker
            Map.Entry<Long, String> entry = virtualServerNode.tailMap(hash, true).firstEntry();
            // 如果 hash 大于 Invoker 在圆环上最大的位置，此时 entry = null，
            // 需要将 TreeMap 的头节点赋值给 entry
            if (entry == null) {
                entry = virtualServerNode.firstEntry();
            }

            // 返回 Invoker
            return entry.getValue();
        }


    }
}
