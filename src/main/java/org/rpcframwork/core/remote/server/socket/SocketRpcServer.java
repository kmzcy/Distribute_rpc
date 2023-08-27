package org.rpcframwork.core.remote.server.socket;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.rpcframwork.core.registry.ServiceList;
import org.rpcframwork.core.registry.ServiceRegistry;
import org.rpcframwork.core.registry.zookeeper.ServiceRegistryImp;
import org.rpcframwork.core.remote.server.RpcServer;
import org.rpcframwork.core.codec.ServiceStatement;
import org.rpcframwork.core.serialize.Serializer;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
import org.rpcframwork.utils.Factory.SingletonFactory;
import org.rpcframwork.utils.Factory.ThreadPoolFactoryUtil;
import org.rpcframwork.utils.ServerShutdownHook;
import org.rpcframwork.utils.exception.RpcException;

public class SocketRpcServer implements RpcServer {
    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    private final Serializer serializer;
    public static final int PORT = 9000;

    public SocketRpcServer() {
        this.threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("SocketServer");
        this.serviceRegistry = SingletonFactory.getInstance(ServiceRegistryImp.class);
        this.serializer = SingletonFactory.getInstance(KryoSerializer.class);

        // 监听zookeeper连接状态
        /*
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
        */
    }

    // 参数service就是interface的implementation object，用于注册一个服务
    public void register(List<ServiceStatement> service) {
        try{
            InetAddress addr = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(addr.toString().replaceAll(".*/", ""), PORT);

            ServiceList serviceList = new ServiceList();
            Map<String, byte[]> serviceStatementList = new HashMap<>();
            System.out.println("inetSocketAddress: " + inetSocketAddress);

            for(ServiceStatement serviceStatement: service){
                String name = serviceStatement.getRpcServiceName();
                System.out.println(name);
                serviceList.put(name, inetSocketAddress);
                serviceStatementList.put(name, serializer.serialize(serviceStatement));
            }

            serviceRegistry.registerService(serviceList, serviceStatementList);

        }catch (UnknownHostException e){
            throw new RpcException("register failed", e);
        }
    }

    public void start() {
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
