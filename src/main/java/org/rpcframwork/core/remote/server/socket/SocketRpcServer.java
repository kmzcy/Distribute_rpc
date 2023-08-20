package org.rpcframwork.core.remote.server.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

import org.rpcframwork.core.registry.RegisterCenter;
import org.rpcframwork.core.remote.server.RpcServer;
import org.rpcframwork.utils.Factory.SingletonFactory;
import org.rpcframwork.utils.exception.RpcException;

public class SocketRpcServer implements RpcServer {
    private final ExecutorService threadPool;
    // interfaceName -> interfaceImplementation object
    private final RegisterCenter registerCenter;
    private final int PORT = 9000;

    public SocketRpcServer() {

        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;

        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
        registerCenter = SingletonFactory.getInstance(RegisterCenter.class);
    }

    // 参数service就是interface的implementation object，用于注册一个服务
    public void register(Object service) {
        try{
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("addr.getAddress(): " + addr.getAddress());
            registerCenter.addService(service, addr);
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
