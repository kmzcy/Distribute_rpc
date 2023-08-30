package org.rpcframwork.core.spring;

import lombok.extern.slf4j.Slf4j;
import org.rpcframwork.core.spring.annotation.RpcScan;
import org.rpcframwork.core.spring.annotation.RpcService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 当某个注解加载时，自定义其加载的方式
 *
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    // 扫描spring 原生的bean
    private static final String SPRING_BEAN_BASE_PACKAGE = "org.rpcframwork.core";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "packageToScan";

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes =
                AnnotationAttributes.fromMap(annotationMetadata
                        .getAnnotationAttributes(RpcScan.class.getName()));
        String[] ScanPackages = new String[0];
        if(annotationAttributes != null){
            ScanPackages = annotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if(ScanPackages.length == 0){
            ScanPackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        CustomScanner rpcServiceScanner = new CustomScanner(registry, RpcService.class);
        CustomScanner SpringBeanScanner = new CustomScanner(registry, Component.class);

        if(resourceLoader != null){
            rpcServiceScanner.setResourceLoader(resourceLoader);
            SpringBeanScanner.setResourceLoader(resourceLoader);
        }
        int rpcServiceAmount = rpcServiceScanner.scan(ScanPackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceAmount);

        int springBeanAmount = SpringBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
