package server.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import request.MoveRequest;
import request.UnjoinRequest;
import server.BadRequestException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ServerResponse;
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
            case MAKE_MOVE -> makeMove(cmd.getAuthToken(), cmd.getGameID(), message, session);
        }
    }//once you open a connection in websocket, this is the main place that messages go

    private void makeMove(String authToken, Integer gameID, String msg, Session session) throws Exception {
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
            ServerMessage loadGame = new LoadGame(updatedGame, null);
            connections.broadcast(null, loadGame); //will send this to everyone
        } catch (Exception ex){
            throwServerError(session, ex);
        }

        //need to check if there is anything to broadcast to the user about game status
        // need to do a loadgame message
    }

    private void throwServerError(Session session, Exception ex) throws IOException {
        var message = ex.getMessage();
        ServerMessage error = new Error(message);
        connections.broadcastToMe(session, error);
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
        try {
            AuthData auth = checkAuth(authToken);
            connections.add(auth.authToken(), gameID, session);
            String username = serviceAuth.getUsername(auth);
            String role = getMyColor(gameID, username);
            String message = String.format("%s has joined the game as %s", username, role);
            ServerMessage notification = new Notification(message);
            connections.broadcast(auth.authToken(), notification); //auth to exclude, ServerMessage to broadcast
            LoadGame loadGame = new LoadGame(serviceGame.getGame(gameID), null);
            connections.broadcastToMe(session, loadGame);
        } catch (Exception ex){
            throwServerError(session, ex);
        }
    }

    private String getMyColor(Integer gameID, String username) throws BadRequestException { //what if they are both the black and white player
        if(serviceGame.getGame(gameID).whiteUsername().equals(username)){
            return "white";
        }
        else if(serviceGame.getGame(gameID).blackUsername().equals(username)){
            return "black";
        }
        else{
            return "an observer";
        }
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