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
    private String version;
    private String group;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
