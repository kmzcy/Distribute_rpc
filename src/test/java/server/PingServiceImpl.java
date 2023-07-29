package server;

import org.rpcframwork.IDL.Hello.HelloRequest;
import org.rpcframwork.IDL.Hello.HelloResponse;
import org.rpcframwork.IDL.Hello.HelloService;
import org.rpcframwork.IDL.ping.PingRequest;
import org.rpcframwork.IDL.ping.PingResponse;
import org.rpcframwork.IDL.ping.PingService;

public class PingServiceImpl implements PingService {

    @Override
    public PingResponse ping(PingRequest request) {
        String name = request.getName();
        String retMsg = "pong: " + name;
        PingResponse response = new PingResponse(retMsg);
        return response;
    }
}
