package org.rpcframwork.core.codec;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
// 返回值编码
public class RpcResponseBody implements Serializable {
    private static final long serialVersionUID = 715745410605631233L;
    private String version;
    private String group;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private String message;
    private Object retObject;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
