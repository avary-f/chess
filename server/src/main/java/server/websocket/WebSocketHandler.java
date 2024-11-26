package server.websocket;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import model.AuthData;
import request.JoinRequest;
import request.MoveRequest;
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

    private void makeMove(String auth, Integer gameID, String msg) throws Exception {
        MakeMove cmdMove = new Gson().fromJson(msg, MakeMove.class);
        ChessMove move = cmdMove.getMove();
        String username = serviceAuth.getUsername(new AuthData(auth, null));
        serviceGame.makeMove(new MoveRequest(auth, gameID, move));
        var message = String.format("%s moved " + convertColumns(move.getStartPosition()) + " to " +
                convertColumns(move.getEndPosition()), username);
        ServerMessage notification = new Notification(message);
        connections.broadcast(auth, notification);
        //need to check if there is anything to broadcast to the user about game status
        // need to do a loadgame message
    }

    private String convertColumns(ChessPosition pos){
        ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));
        return columns.get(pos.getColumn()) + pos.getRow();
    }

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

}