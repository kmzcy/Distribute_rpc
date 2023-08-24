package org.rpcframwork.core.rpc_protocol;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ServiceStatement {
    /**
     * 服务的版本
     */
    private String version = "";
    /**
     * 当接口存在多种实现类时，通过group对不同的实现进行区分
     */
    private String group = "";
    /**
     * 提供服务的接口
     */
    private String interfaceName = "";
    /**
     * 目标服务
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
