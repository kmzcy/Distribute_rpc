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
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
}
