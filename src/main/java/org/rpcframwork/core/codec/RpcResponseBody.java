package org.rpcframwork.core.codec;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
// 返回值编码
public class RpcResponseBody implements Serializable {
    private Object retObject;
}
