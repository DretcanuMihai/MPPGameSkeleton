package myapp.network.rpcprotocol;

import myapp.model.entities.Game;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.services.ServiceException;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IObserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class RpcServerProxy implements ISuperService {
    private final String host;
    private final int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private final BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public RpcServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public Game login(User user, IObserver observer) throws ServiceException {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.LOGIN_GAME) {
            this.client = observer;
            return (Game) response.data();
        } else{
            String err = response.data().toString();
            closeConnection();
            throw new ServiceException(err);
        }
    }


    @Override
    public void logout(User user) throws ServiceException {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
    }


    @Override
    public Game nextRound(User user, Round round) throws ServiceException {
        Object[] data = new Object[2];
        data[0] = user;
        data[1] = round;
        Request req = new Request.Builder().type(RequestType.NEXT_ROUND).data(data).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        return (Game) response.data();
    }

    @Override
    public List<Game> getLeaderBoard() throws ServiceException {
        Request req = new Request.Builder().type(RequestType.GET_LEADER_BOARD).data(null).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        Game[] data = (Game[]) response.data();
        return Arrays.stream(data).toList();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            System.err.println("Error closing the connection;");
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) throws ServiceException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.GAME_FINISHED) {
            Game game = (Game) response.data();
            System.out.println("Game finished:" + game.toString());
            client.gameEnded(game);
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.GAME_FINISHED;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received:" + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
