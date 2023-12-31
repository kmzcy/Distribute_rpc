package org.rpcframwork.core.remote.client;


public interface RpcClientProxyBuilder {
    /**
     * 获取要求服务clazz的代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T getProxy(Class<T> clazz);
}
