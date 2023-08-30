package org.rpcframwork.core.remote.server.socket;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;
import org.rpcframwork.core.registry.ServiceList;
import org.rpcframwork.core.registry.ServiceRegistry;
import org.rpcframwork.core.registry.zookeeper.ZkServiceRegistryImp;
import org.rpcframwork.core.remote.server.RpcServer;
import org.rpcframwork.core.codec.ServiceStatement;
import org.rpcframwork.core.serialize.Serializer;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.core.spring.annotation.RpcService;
import org.rpcframwork.utils.Factory.SingletonFactory;
import org.rpcframwork.utils.Factory.ThreadPoolFactoryUtil;
import org.rpcframwork.utils.ServerShutdownHook;
import org.rpcframwork.utils.exception.RpcException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketRpcServer implements RpcServer {

    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    private final Serializer serializer;

    private List<ServiceStatement> serviceCache = new ArrayList<>();
    public static final int PORT = 9000;

    public SocketRpcServer() {
        this.threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("SocketServer");
        this.serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistryImp.class);
        this.serializer = SingletonFactory.getInstance(KryoSerializer.class);
    }

    /**
     * 将一个服务从serviceCache中移除
     * @param serviceProvider
     */
    public void deRegister(Object serviceProvider){
        serviceCache.remove(serviceProvider);
    }

    /**
     * 将一个服务加入serviceCache
     * @param serviceProvider interface 的 implementation object，用于提供服务
     */
    public void register(Object serviceProvider){
        RpcService rpcService = serviceProvider.getClass().getAnnotation(RpcService.class);
        ServiceStatement helloServiceStatement = ServiceStatement.builder()
                .version(rpcService.version())
                .group(rpcService.group())
                .interfaceName(serviceProvider.getClass().getInterfaces()[0].getName())
                .service(serviceProvider)
                .build();
        log.info("[{}] has been add to the serviceCache", helloServiceStatement.getRpcServiceName());
        serviceCache.add(helloServiceStatement);
    }

    /**
     * 监听与服务中心的连接状态
     */
    private void connectStateWatcher(){
        threadPool.execute(()->{
            while (true){
                try{
                    Thread.sleep(3000);
                    System.out.println(serviceRegistry.isConnected());
                }catch (InterruptedException e){
                    System.out.println("InterruptedException");
                    break;
                }
            }
        });
    }

    /**
     * 将缓存（cache）中的所有服务注册到注册中心
     * @param service
     */
    private void registerInCenter(List<ServiceStatement> service) {
        try{
            InetAddress addr = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(addr.toString().replaceAll(".*/", ""), PORT);

            ServiceList serviceList = new ServiceList();
            Map<String, byte[]> serviceStatementList = new HashMap<>();

            for(ServiceStatement serviceStatement: service){
                String name = serviceStatement.getRpcServiceName();
                serviceList.put(name, inetSocketAddress);
                serviceStatementList.put(name, serializer.serialize(serviceStatement));
            }

            serviceRegistry.registerService(serviceList, serviceStatementList);

        }catch (UnknownHostException e){
            throw new RpcException("register failed", e);
        }
    }

    public void start() {
        registerInCenter(serviceCache);
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("server starting...");
            ServerShutdownHook.getCustomShutdownHook().clearAll(serviceRegistry);
            Socket handleSocket;
            while ((handleSocket = serverSocket.accept()) != null) {
                System.out.println("client connected, ip:" + handleSocket.getInetAddress());
                //新线程的执行
                threadPool.execute(new SocketRpcServerWorker(handleSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
