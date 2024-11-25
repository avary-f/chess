package server.websocket;

import com.google.gson.Gson;
//import dataaccess.DataAccess;
import model.AuthData;
import request.JoinRequest;
import request.UnjoinRequest;
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
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case CONNECT -> connect(cmd.getAuthToken(), cmd.getGameID(), session);
            case LEAVE -> leave(cmd.getAuthToken(), cmd.getGameID());
        }
    }
    //once you open a connection in websocket, this is the main place that messages go

    private void connect(String auth, Integer gameID, Session session) throws IOException {
        connections.add(auth, gameID, session);
        String username = serviceAuth.getUsername(new AuthData(auth, null));
        String message = String.format("%s has joined the game", username);
        ServerMessage notification = new Notification(message);
        connections.broadcast(auth, notification); //auth to exclude, ServerMessage to broadcast
    }

    private void leave(String auth, Integer gameID) throws Exception {
        serviceGame.unjoinGame(new UnjoinRequest(auth, gameID));
        connections.remove(auth);
        String username = serviceAuth.getUsername(new AuthData(auth, null));
        var message = String.format("%s has left the game", username);
        ServerMessage notification = new Notification(message);
        connections.broadcast(auth, notification);
    }
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