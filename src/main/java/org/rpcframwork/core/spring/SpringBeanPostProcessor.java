package org.rpcframwork.core.spring;

import lombok.extern.slf4j.Slf4j;
import org.rpcframwork.core.codec.RpcRequestBody;
import org.rpcframwork.core.remote.client.RpcClientProxyBuilder;
import org.rpcframwork.core.remote.client.RpcClientTransfer;
import org.rpcframwork.core.remote.client.socket.SocketRpcClientProxyBuilderBuilder;
import org.rpcframwork.core.remote.client.socket.SocketRpcClientTransfer;
import org.rpcframwork.core.remote.handler.EventIdProvider;
import org.rpcframwork.core.spring.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    public SpringBeanPostProcessor(){
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info("Bean add into container  [{}]", beanName);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for(Field declaredField : declaredFields){
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if(rpcReference != null){
                // 构建请求体
                // .builder() 它其实是一种设计模式，叫做建造者模式，它的含义是将一个复杂的对象的构建与它的表示分离，同样的构建过程可以创建不同的表示
                // 这里使用了lombok进行了优化
                // 创建管理器，管理requestId
                RpcRequestBody requestBody = RpcRequestBody.builder()
                                        .requestId(EventIdProvider.getEventId())
                                        .version(rpcReference.version())
                                        .group(rpcReference.group())
                                        .build();
                RpcClientTransfer RpcClientTransfer = new SocketRpcClientTransfer();
                RpcClientProxyBuilder proxyBuilder = new SocketRpcClientProxyBuilderBuilder(RpcClientTransfer, requestBody);
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, proxyBuilder.getProxy(declaredField.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }
}
