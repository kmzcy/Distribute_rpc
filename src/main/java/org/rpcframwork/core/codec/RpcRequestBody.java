package org.rpcframwork.core.codec;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

// 调用编码
public class RpcRequestBody implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String version;
    private String group;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.interfaceName;
    }
}
