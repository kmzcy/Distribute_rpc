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
    private Object retObject;
}
