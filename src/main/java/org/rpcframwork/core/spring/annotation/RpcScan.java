package org.rpcframwork.core.spring.annotation;

import org.rpcframwork.core.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Import(CustomScannerRegistrar.class)

public @interface RpcScan {
    String[] packageToScan();
}
