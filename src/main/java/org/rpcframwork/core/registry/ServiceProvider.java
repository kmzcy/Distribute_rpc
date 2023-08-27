package org.rpcframwork.core.registry;

import org.rpcframwork.core.codec.ServiceStatement;

public interface ServiceProvider {

    /**
     * @param rpcServiceName org.rpcframwork.IDL.Hello.HelloServicegroup1version1/127.0.0.1:9999
     * @return service object
     */
    byte[] getService(String rpcServiceName);

}
