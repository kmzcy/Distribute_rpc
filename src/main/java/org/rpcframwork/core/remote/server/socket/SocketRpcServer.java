package org.rpcframwork.core.remote.server.socket;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

import org.rpcframwork.core.registry.ServiceRegistry;
import org.rpcframwork.core.registry.zookeeper.ServiceRegistryImp;
import org.rpcframwork.core.remote.server.RpcServer;
import org.rpcframwork.core.rpc_protocol.ServiceStatement;
import org.rpcframwork.utils.Factory.SingletonFactory;
import org.rpcframwork.utils.exception.RpcException;

public class SocketRpcServer implements RpcServer {
    private final ExecutorService threadPool;
    // interfaceName -> interfaceImplementation object
    private final ServiceRegistry serviceRegistry;
    private final int PORT = 9000;

    public SocketRpcServer() {

        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;

        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
        this.serviceRegistry = SingletonFactory.getInstance(ServiceRegistryImp.class);
    }

    // 参数service就是interface的implementation object，用于注册一个服务
    public void register(ServiceStatement service) {
        try{
            InetAddress addr = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(addr, PORT);
            System.out.println("inetSocketAddress.getAddress().getHostAddress(): " + inetSocketAddress.getAddress().getHostAddress());
        }catch (UnknownHostException e){
            throw new RpcException("register failed", e);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("server starting...");
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
