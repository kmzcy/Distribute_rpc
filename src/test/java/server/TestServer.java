package server;

import org.rpcframwork.IDL.Hello.HelloService;
import org.rpcframwork.core.remote.server.socket.SocketRpcServer;
import org.rpcframwork.core.spring.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@RpcScan(packageToScan = {"server"})
public class TestServer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestServer.class);

        SocketRpcServer socketrpcServer = applicationContext.getBean(SocketRpcServer.class);
        HelloService helloService = applicationContext.getBean(HelloServiceImpl.class);
        socketrpcServer.register(helloService);
        socketrpcServer.start();
    }
}
