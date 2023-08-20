package org.rpcframwork.core.remote.server.socket;

import org.rpcframwork.core.remote.handler.ServerRequestHandler;
import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;

import org.rpcframwork.utils.Factory.SingletonFactory;

import java.io.*;
import java.net.Socket;

public class SocketRpcServerWorker implements Runnable{ //继承runnable，作为一个线程

    private Socket socket;
    private final ServerRequestHandler serverRequestHandler;

    public SocketRpcServerWorker(Socket socket) {
        this.socket = socket;
        serverRequestHandler = SingletonFactory.getInstance(ServerRequestHandler.class);
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            RpcResponse rpcResponse = serverRequestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(rpcResponse);
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
