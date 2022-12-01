package myapp.network.utils;

import myapp.network.rpcprotocol.RpcReflectionWorker;
import myapp.services.interfaces.ISuperService;

import java.net.Socket;


public class AppConcurrentServer extends AbstractConcurrentServer {
    private final ISuperService superService;

    public AppConcurrentServer(int port, ISuperService superService) {
        super(port);
        this.superService = superService;
    }

    @Override
    protected Thread createWorker(Socket client) {
        RpcReflectionWorker worker = new RpcReflectionWorker(superService, client);
        return new Thread(worker);
    }
}
