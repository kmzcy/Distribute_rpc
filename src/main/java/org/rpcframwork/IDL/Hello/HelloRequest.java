package org.rpcframwork.IDL.Hello;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HelloRequest implements Serializable {
    private String name;
}
