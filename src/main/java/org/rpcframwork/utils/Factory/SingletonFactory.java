package org.rpcframwork.utils.Factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class SingletonFactory {
    // 为了线程安全，使用ConcurrentHashMap
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> c){
        if(c == null){
            throw new IllegalArgumentException();
        }
        String key = c.toString();
        if(OBJECT_MAP.containsKey(key)){
            return c.cast(OBJECT_MAP.get(key));
        } else{
            return c.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return c.getDeclaredConstructor().newInstance();
                }catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }
}
