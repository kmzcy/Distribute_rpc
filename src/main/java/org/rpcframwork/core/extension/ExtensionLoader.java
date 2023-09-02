package org.rpcframwork.core.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ExtensionLoader<T> {
    private static final String SERVICE_DIRECTORY = "META-INF/RPC/";

    // 用与存储每个已经建立过的ExtensionLoader
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();
    // 当前的所有extension类的map
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Class<?> type;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        if(type == null)
            throw new IllegalArgumentException("Extension type == null");
        if(!type.isInterface())
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        if(!withExtensionAnnotation(type))
            throw new IllegalArgumentException("Extension type(" + type +
                    ") is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");

        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        // 保证单例
        if(loader == null){
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return  loader;

    }

    /**
     * 检查是否有SPI注解
     * @param type
     * @return
     * @param <T>
     */
    private static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SPI.class);
    }

    /**
     *
     * @param name
     * @return
     */
    public T getExtension(String name){
        // cachedInstances 当前的所有extension类的map
        if (name.length() == 0 || name == null) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if(holder == null){
            cachedInstances.putIfAbsent(name, new Holder<Object>());
            holder = cachedInstances.get(name);
        }

        Object instance = holder.get();
        if(instance == null){
            synchronized (holder){
                instance = holder.get();
                if (instance == null){
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }

        return (T)instance;
    }

    private T createExtension(String name) {
        // load all extension classes of type T from file and get specific one by name
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        // get the loaded extension class from the cache
        Map<String, Class<?>> classes = cachedClasses.get();
        // double check
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // load all extensions from our extensions directory
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        // SERVICE_DIRECTORY = "META-INF/RPC/"
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            // Enumeration 枚举类接口
            Enumeration<URL> urls;
            // 获取本类的类加载器
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            // 获取META-INF/RPC/目录下的文件
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    // 一个一个文件进行加载
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 从 META-INF 中读取行，并从行中获取类名 eg: loadBalance=github.javaguide.loadbalance.loadbalancer.ConsistentHashLoadBalance
     * @param extensionClasses
     * @param classLoader
     * @param resourceUrl
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null){
                // 排除’#‘后的内容
                final int ci = line.indexOf('#');
                if (ci >= 0){
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0){
                    try{
                        final int ei = line.indexOf('=');
                        // loadBalance
                        String name = line.substring(0, ei).trim();
                        // github.javaguide.loadbalance.loadbalancer.ConsistentHashLoadBalance
                        String clazzName = line.substring(ei+1).trim();
                        if(name.length() > 0 && clazzName.length() > 0){
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            // 放入extensionClasses
                            extensionClasses.put(name, clazz);
                        }
                    }catch (ClassNotFoundException e){
                        log.error(e.getMessage());
                    }
                }
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
