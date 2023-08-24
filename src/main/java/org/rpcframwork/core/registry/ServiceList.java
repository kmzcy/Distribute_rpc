package org.rpcframwork.core.registry;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册服务，服务的标识为：InterfaceName/version/group）
 *
 *  String: rpc service name(service.getInterfaceName())
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
