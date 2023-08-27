package org.rpcframwork.core.registry;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册服务，服务的标识（rpc service name）为：org.rpcframwork.IDL.Hello.HelloServicegroup1version1
 *
 *  String: rpc service name eg:/distribute_rpc/org.rpcframwork.IDL.Hello.HelloServicegroup1version1
 *                           rpcServiceName 对应 ServiceStatement 中的 getRpcServiceName()
 *  InetSocketAddress: inetSocketAddress service address
 */

public class ServiceList extends HashMap<String, InetSocketAddress> {
    public ServiceList(){
        super();
    }
    public ServiceList(Map<String,InetSocketAddress> m){
        super(m);
    }

    @Override
    public String toString(){
        String result = null;
        for(String key : keySet()){
            result = result + "Service: " + key +
                    " address: " + get(key).getAddress().toString() +
                    " port:" + get(key).getPort();
        }

        return result;
    }
}
