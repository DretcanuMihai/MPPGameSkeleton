package myapp.network.utils;

import java.net.Socket;


public abstract class AbstractConcurrentServer extends AbstractServer {

    public AbstractConcurrentServer(int port) {
        super(port);
    }

    protected void processRequest(Socket client) {
        Thread workerThread = createWorker(client);
        workerThread.start();
    }

    protected abstract Thread createWorker(Socket client);


}
