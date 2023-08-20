package org.rpcframwork.core.registry;

import java.net.InetAddress;
import java.util.HashMap;

public class RegisterCenter {
    // 用于保存服务的map key 为：service.getClass().getInterfaces()[0].getName() 获得service对象所实现的第一个接口的名字
    private final HashMap<String, Object> registeredService = new HashMap<>();

    // 改成InetSocketAddress
    private final HashMap<String, InetAddress> registeredServiceAddress = new HashMap<>();
    public void addService(Object service, InetAddress address) {
        registeredService.put(service.getClass().getInterfaces()[0].getName(), service);
        registeredServiceAddress.put(service.getClass().getInterfaces()[0].getName(), address);
    }

    public Object getService(String rpcServiceName){
        return registeredService.get(rpcServiceName);
    }

    public Object getServiceAddress(String rpcServiceName){
        return registeredServiceAddress.get(rpcServiceName);
    }
}
