package org.rpcframwork.core.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;

public class RpcServer {
    private final ExecutorService threadPool;
    // interfaceName -> interfaceImplementation object

    // 用于保存服务的map key 为：service.getClass().getInterfaces()[0].getName() 获得service对象所实现的第一个接口的名字
    private final HashMap<String, Object> registeredService;

    public RpcServer() {

        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;

        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);

        this.registeredService = new HashMap<String, Object>();
    }

    // 参数service就是interface的implementation object
    public void register(Object service) {
        registeredService.put(service.getClass().getInterfaces()[0].getName(), service);
    }

    public void serve(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("server starting...");
            Socket handleSocket;
            while ((handleSocket = serverSocket.accept()) != null) {
                System.out.println("client connected, ip:" + handleSocket.getInetAddress());
                //新线程的执行
                threadPool.execute(new RpcServerWorker(handleSocket, registeredService));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
