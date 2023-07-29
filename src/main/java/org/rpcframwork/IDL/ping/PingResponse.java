package org.rpcframwork.IDL.ping;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
@AllArgsConstructor
public class PingResponse implements Serializable {
    private String msg;
}