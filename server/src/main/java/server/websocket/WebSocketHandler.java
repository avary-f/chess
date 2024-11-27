package server.websocket;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import request.JoinRequest;
import request.MoveRequest;
import request.UnjoinRequest;
import server.BadRequestException;
import server.ServerResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.GameService;
import service.UserService;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            case MAKE_MOVE -> makeMove(cmd.getAuthToken(), cmd.getGameID(), message);
        }
    }//once you open a connection in websocket, this is the main place that messages go

    private void makeMove(String authToken, Integer gameID, String msg) throws Exception {
        try{
            AuthData auth = checkAuth(authToken);
            MakeMove cmdMove = new Gson().fromJson(msg, MakeMove.class);
            ChessMove move = cmdMove.getMove();
            String username = serviceAuth.getUsername(new AuthData(auth.authToken(), null));
            GameData updatedGame = serviceGame.makeMove(new MoveRequest(auth.authToken(), gameID, move));
            var message = String.format("%s moved " + convertColumns(move.getStartPosition()) + " to " +
                    convertColumns(move.getEndPosition()), username.toUpperCase());
            ServerMessage notification = new Notification(message);
            connections.broadcast(auth.authToken(), notification);
            ServerMessage loadGame = new LoadGame(updatedGame, message);
            connections.broadcast(auth.authToken(), loadGame); //will send this to everyone else
            var messageToMe = String.format("You moved " + convertColumns(move.getStartPosition()) + " to " +
                    convertColumns(move.getEndPosition()), username.toUpperCase());
            ServerMessage loadGameToMe = new LoadGame(updatedGame, messageToMe);
            connections.broadcastToMe(auth.authToken(), loadGameToMe);
        } catch (Exception ex){
            var message = "Server Error: unauthorized";
            ServerMessage error = new Error(message);
            connections.broadcastToMe(authToken, error);
        }

        //need to check if there is anything to broadcast to the user about game status
        // need to do a loadgame message
    }

    private String convertColumns(ChessPosition pos){
        ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));
        return columns.get(pos.getColumn()) + pos.getRow();
    }

    private AuthData checkAuth(String authToken) throws Exception {
        AuthData auth = new AuthData(authToken, null);
        serviceAuth.checkAuthTokenValid(auth);
        return auth; //returns it if it exists
    }

    private void connect(String authToken, Integer gameID, Session session) throws Exception {
        AuthData auth = checkAuth(authToken);
        connections.add(auth.authToken(), gameID, session);
        String username = serviceAuth.getUsername(auth);
        String message = String.format("%s has joined the game", username);
        ServerMessage notification = new Notification(message);
        connections.broadcast(auth.authToken(), notification); //auth to exclude, ServerMessage to broadcast
        LoadGame loadGame = new LoadGame(serviceGame.getGame(gameID), "You have joined the game");
        connections.broadcastToMe(auth.authToken(), loadGame);
    }

    private void leave(String authToken, Integer gameID) throws Exception {
        AuthData auth = checkAuth(authToken);
        serviceGame.unjoinGame(new UnjoinRequest(auth.authToken(), gameID));
        connections.remove(auth.authToken());
        String username = serviceAuth.getUsername(auth);
        var message = String.format("%s has left the game", username);
        ServerMessage notification = new Notification(message);
        connections.broadcast(auth.authToken(), notification);
    }

}