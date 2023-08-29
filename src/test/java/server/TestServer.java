package server;

import org.rpcframwork.IDL.Hello.HelloService;
import org.rpcframwork.core.remote.server.socket.SocketRpcServer;

public class TestServer {
    public static void main(String[] args) {
        SocketRpcServer socketrpcServer = new SocketRpcServer(); // 真正的rpc server
        HelloService helloService = new HelloServiceImpl(); // 包含需要处理的方法的对象
        socketrpcServer.register(helloService);
        socketrpcServer.start();
    }
}
