package server.websocket;

import com.google.gson.Gson;
//import dataaccess.DataAccess;
import model.AuthData;
import server.ServerResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.GameService;
import service.UserService;
import websocket.messages.Notification;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private UserService serviceUser;
    private GameService serviceGame;
    private AuthService serviceAuth;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(UserService serviceUser, GameService serviceGame){
        this.serviceUser = serviceUser;
        this.serviceAuth = serviceUser.getServiceAuth();
        this.serviceGame = serviceGame;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case CONNECT -> connect(cmd.getAuthToken(), session);
//            case EXIT -> exit(action.visitorName());
        }
    }

    private void connect(String auth, Session session) throws IOException {
        System.out.println("Testing");
        connections.add(auth, session);
        String username = serviceAuth.getUsername(new AuthData(auth, null));
        String message = String.format("%s has joined the game", username);
        Notification notification = new Notification(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(username, notification);
    }

//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}