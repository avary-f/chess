package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import request.EndGameRequest;
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
import websocket.messages.*;
import websocket.commands.*;
import websocket.messages.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebSocket
public class WebSocketHandler {

    private final GameService serviceGame;
    private final AuthService serviceAuth;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(UserService serviceUser, GameService serviceGame){
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
            case RESIGN -> resign(cmd.getAuthToken(), cmd.getGameID(), session);
            case REDRAW -> redraw(cmd.getAuthToken(), cmd.getGameID(), session);
            case HIGHLIGHT -> highlight(cmd.getAuthToken(), cmd.getGameID(), message, session);
        }
    }//once you open a connection in websocket, this is the main place that messages go

    private void resign(String authToken, Integer gameID, Session session) throws Exception {
        try{
            GameData game = serviceGame.getGame(gameID);
            if(game.game.isGameEnded()){
                var message = "Error: Game already ended";
                ServerMessage error = new Error(message);
                connections.broadcastToMe(session, error);
            }
            AuthData auth = checkAuth(authToken);
            endGame(auth, gameID, true);
            String username = serviceAuth.getUsername(auth);
            var message = String.format("Game over: %s resigned from the game", username);
            ServerMessage notification = new Notification(message);
            connections.broadcast(auth.authToken(), notification, gameID);
            var newMessage = "Game over: you resigned from the game";
            connections.broadcastToMe(session, new Notification(newMessage));
        } catch (Exception ex){
            throwServerError(session, ex);
        }
    }

    private void highlight(String authToken, Integer gameID, String msg, Session session) throws Exception {
        checkAuth(authToken);
        Highlight cmdMove = new Gson().fromJson(msg, Highlight.class);
        String highlightSquare = cmdMove.getHighlightSquare();
        ServerMessage loadGameHighlight = new LoadGameHighlight(serviceGame.getGame(gameID), null, highlightSquare);
        connections.broadcastToMe(session, loadGameHighlight);
    }

    private void redraw(String authToken, Integer gameID, Session session) throws Exception{
        checkAuth(authToken);
        ServerMessage loadGame = new LoadGame(serviceGame.getGame(gameID), null);
        connections.broadcastToMe(session, loadGame);
    }

    private void makeMove(String authToken, Integer gameID, String msg, Session session) throws Exception {
        try{
            AuthData auth = checkAuth(authToken);
            GameData game = serviceGame.getGame(gameID);
            if(game.game.isGameEnded()){
                var message = "Error: Game ended, no more moves can be made";
                ServerMessage error = new Error(message);
                connections.broadcastToMe(session, error);
            }
            else {
                MakeMove cmdMove = new Gson().fromJson(msg, MakeMove.class);
                ChessMove move = cmdMove.getMove();
                String username = serviceAuth.getUsername(new AuthData(auth.authToken(), null));
                GameData updatedGame = serviceGame.makeMove(new MoveRequest(auth.authToken(), gameID, move));
                var message = String.format("%s moved " + convertColumns(move.getStartPosition()) + " to " +
                        convertColumns(move.getEndPosition()), username.toUpperCase());
                ServerMessage notification = new Notification(message);
                connections.broadcast(auth.authToken(), notification, gameID);
                ServerMessage loadGame = new LoadGame(updatedGame, null);
                connections.broadcast(null, loadGame, gameID);//will send this to everyone
                ChessGame.TeamColor otherTeamColor = getOtherTeamColor(gameID, username);
                if (checkForCheckmate(updatedGame, otherTeamColor)) {
                    sendGameOverCheckmateNotification(auth, gameID);
                }
                else if (checkForCheck(updatedGame, otherTeamColor)) {
                    sendInCheckNotification(updatedGame, otherTeamColor);
                }
                else if (checkForStalemate(updatedGame, otherTeamColor)) {
                    sendGameOverStalemateNotification(auth, gameID);
                }
            }
        } catch (InvalidMoveException ex){
            var message = ex.getMessage();
            ServerMessage error = new Error(message);
            connections.broadcastToMe(session, error);
        } catch (IOException ex) {
            var message = "Connection closed due to inactivity. Please retype your command";
            ServerMessage error = new Error(message);
            connections.broadcastToMe(session, error);
        } catch (BadRequestException ex){
            var message = "Error: Cannot make a move for the other team";
            ServerMessage error = new Error(message);
            connections.broadcastToMe(session, error);
        } catch (Exception ex){
            throwServerError(session, ex);
        }
    }

    private boolean checkForCheckmate(GameData updatedGame, ChessGame.TeamColor otherTeamColor) throws BadRequestException {
        ChessGame game = serviceGame.getGame(updatedGame.gameID()).game;
        return game.isInCheckmate(otherTeamColor);
    }

    private boolean checkForStalemate(GameData updatedGame, ChessGame.TeamColor otherTeamColor) throws BadRequestException {
        ChessGame game = serviceGame.getGame(updatedGame.gameID()).game;
        return game.isInStalemate(otherTeamColor);
    }

    private void sendGameOverCheckmateNotification(AuthData auth, Integer gameID) throws Exception {
        String winnerUsername = endGame(auth, gameID, false); //not resigning, still in play
        String loserUsername = getUsername(gameID, getOtherTeamColor(gameID, winnerUsername));
        String newMessage = String.format("%s is in checkmate. Game over, %s won!", loserUsername, winnerUsername);
        ServerMessage newNotification = new Notification(newMessage);
        connections.broadcast(null, newNotification, gameID);
    }

    private void sendGameOverStalemateNotification(AuthData auth, Integer gameID) throws Exception {
        String winnerUsername = endGame(auth, gameID, false); //not resigning, still in play
        String loserUsername = getUsername(gameID, getOtherTeamColor(gameID, winnerUsername));
        String newMessage = String.format("Game ended in a stalemate. Both %s and %s tied!", loserUsername, winnerUsername);
        ServerMessage newNotification = new Notification(newMessage);
        connections.broadcast(null, newNotification, gameID);
    }

    private void sendInCheckNotification(GameData updatedGame, ChessGame.TeamColor otherTeamColor) throws IOException {
        String otherUsername;
        if(otherTeamColor.equals(ChessGame.TeamColor.WHITE)){
            otherUsername = updatedGame.whiteUsername();
        }
        else{
            otherUsername = updatedGame.blackUsername();
        }
        String message = String.format("%s is in check", otherUsername);
        ServerMessage newNotification = new Notification(message);
        connections.broadcast(null, newNotification, updatedGame.gameID());
    }

    private boolean checkForCheck(GameData game, ChessGame.TeamColor otherTeamColor) {
        return game.game.isInCheck(otherTeamColor);
    }

    private void throwServerError(Session session, Exception ex) throws IOException {
        var message = ex.getMessage();
        ServerMessage error = new Error(message);
        connections.broadcastToMe(session, error);
    }

    private String convertColumns(ChessPosition pos){
        ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));
        return columns.get(pos.getColumn() - 1) + pos.getRow();
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
            connections.broadcast(auth.authToken(), notification, gameID); //auth to exclude, ServerMessage to broadcast
            LoadGame loadGame = new LoadGame(serviceGame.getGame(gameID), null);
            connections.broadcastToMe(session, loadGame);
        } catch (Exception ex){
            throwServerError(session, ex);
        }
    }

    private String getMyColor(Integer gameID, String username) throws BadRequestException { //what if they are both the black and white player
        if(serviceGame.getGame(gameID).whiteUsername() != null && serviceGame.getGame(gameID).whiteUsername().equals(username)){
            return "white";
        }
        else if(serviceGame.getGame(gameID).blackUsername() != null && serviceGame.getGame(gameID).blackUsername().equals(username)){
            return "black";
        }
        else{
            return "an observer";
        }
    }

    private ChessGame.TeamColor getOtherTeamColor(Integer gameID, String username) throws BadRequestException {
        String myColor = getMyColor(gameID, username);
        if(myColor.equals("white")){
            return ChessGame.TeamColor.BLACK;
        }
        else{
            return ChessGame.TeamColor.WHITE;
        }
    }

    private String getUsername(Integer gameID, ChessGame.TeamColor otherTeamColor) throws BadRequestException {
        GameData game = serviceGame.getGame(gameID);
        if(otherTeamColor.equals(ChessGame.TeamColor.BLACK)){
            return game.blackUsername();
        }
        else{
            return game.whiteUsername();
        }
    }

    private void leave(String authToken, Integer gameID) throws Exception {
        AuthData auth = checkAuth(authToken);
        serviceGame.unjoinGame(new UnjoinRequest(auth.authToken(), gameID));
        connections.remove(auth.authToken());
        String username = serviceAuth.getUsername(auth);
        var message = String.format("%s has left the game", username);
        ServerMessage notification = new Notification(message);
        connections.broadcast(auth.authToken(), notification, gameID);
    }

    private String endGame(AuthData auth, Integer gameID, boolean resigning) throws Exception {
        serviceGame.endGame(new EndGameRequest(auth.authToken(), gameID, resigning)); //make the resigner lose
        ChessGame.TeamColor winnerColor = serviceGame.getGame(gameID).game.getWinnerUser();
        if(winnerColor.equals(ChessGame.TeamColor.WHITE)){
            return serviceGame.getGame(gameID).whiteUsername();
        }
        else{
            return serviceGame.getGame(gameID).blackUsername();
        }
    }

}