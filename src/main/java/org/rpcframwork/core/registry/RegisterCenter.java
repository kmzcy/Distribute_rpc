package org.rpcframwork.core.registry;

import java.util.HashMap;

public class RegisterCenter {
    // 用于保存服务的map key 为：service.getClass().getInterfaces()[0].getName() 获得service对象所实现的第一个接口的名字
    private final HashMap<String, Object> registeredService = new HashMap<String, Object>();;
    public void addService(Object service) {
        registeredService.put(service.getClass().getInterfaces()[0].getName(), service);
    }

    public Object getService(String rpcServiceName){
        return registeredService.get(rpcServiceName);
    }
}
