package myapp.network.rpcprotocol;

import myapp.model.entities.Game;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.services.ServiceException;
import myapp.services.interfaces.IObserver;
import myapp.services.interfaces.ISuperService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;


public class RpcReflectionWorker implements Runnable, IObserver {
    private final ISuperService server;
    private final Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public RpcReflectionWorker(ISuperService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        do {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (connected);
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameEnded(Game game) {
        Response resp = new Response.Builder().type(ResponseType.GAME_FINISHED).data(game).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + request.type();
        System.out.println("HandlerName: " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Inside handleLOGIN");
        try {
            User user = (User) request.data();
            Game game = server.login(user, this);
            connected = true;
            return new Response.Builder().type(ResponseType.LOGIN_GAME).data(game).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Inside handleLOGOUT");
        try {
            User user = (User) request.data();
            server.logout(user);
            connected = false;
            return okResponse;
        } catch (ServiceException e) {
            e.printStackTrace();
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleNEXT_ROUND(Request request) {
        System.out.println("Inside handleNEXT_ROUND");
        try {
            Object[] data = (Object[]) request.data();
            User user = (User) data[0];
            Round round = (Round) data[1];
            Game game = server.nextRound(user, round);
            return new Response.Builder().type(ResponseType.NEXT_ROUND_GAME).data(game).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_LEADER_BOARD(Request request) {
        System.out.println("Inside handleGET_LEADER_BOARD");
        try {
            Game[] games = server.getLeaderBoard().toArray(new Game[0]);
            return new Response.Builder().type(ResponseType.GET_LEADER_BOARD).data(games).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    synchronized private void sendResponse(Response response) throws IOException {
        System.out.println("Sending response:" + response);
        output.writeObject(response);
        output.flush();
    }
}
