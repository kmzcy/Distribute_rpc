package org.rpcframwork.core.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;

import org.rpcframwork.core.registry.RegisterCenter;
import org.rpcframwork.utils.Factory.SingletonFactory;

public class RpcServer {
    private final ExecutorService threadPool;
    // interfaceName -> interfaceImplementation object


    private final RegisterCenter registerCenter;

    public RpcServer() {

        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;

        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);

        registerCenter = SingletonFactory.getInstance(RegisterCenter.class);
    }

    // 参数service就是interface的implementation object
    public void register(Object service) {
        registerCenter.addService(service);
    }

    public void serve(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("server starting...");
            Socket handleSocket;
            while ((handleSocket = serverSocket.accept()) != null) {
                System.out.println("client connected, ip:" + handleSocket.getInetAddress());
                //新线程的执行
                threadPool.execute(new RpcServerWorker(handleSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
