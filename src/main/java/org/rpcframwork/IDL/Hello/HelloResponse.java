package org.rpcframwork.IDL.Hello;

import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HelloResponse implements Serializable {
    private String msg;
}
