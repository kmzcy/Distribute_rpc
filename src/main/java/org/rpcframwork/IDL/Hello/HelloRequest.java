package org.rpcframwork.IDL.Hello;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HelloRequest implements Serializable {
    private String name;
}
