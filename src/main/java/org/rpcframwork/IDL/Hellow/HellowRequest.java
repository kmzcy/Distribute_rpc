package org.rpcframwork.IDL.Hellow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HellowRequest implements Serializable {
    private String name;
}
