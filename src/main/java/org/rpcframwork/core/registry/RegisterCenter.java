package org.rpcframwork.core.registry;

import org.rpcframwork.core.rpc_protocol.ServiceStatement;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class RegisterCenter {
    // 用于保存服务的map key 为：service.getClass().getInterfaces()[0].getName() 获得service对象所实现的第一个接口的名字
    private final HashMap<String, ServiceStatement> registeredService = new HashMap<>();

    // 改成InetSocketAddress
    private final HashMap<String, InetSocketAddress> registeredServiceAddress = new HashMap<>();
    public void addService(ServiceStatement service, InetSocketAddress address) {
        registeredService.put(service.getInterfaceName(), service);
        registeredServiceAddress.put(service.getInterfaceName(), address);
    }

    public ServiceStatement getService(String rpcServiceName){
        return registeredService.get(rpcServiceName);
    }

    public InetSocketAddress getServiceAddress(String rpcServiceName){
        return registeredServiceAddress.get(rpcServiceName);
    }
}
