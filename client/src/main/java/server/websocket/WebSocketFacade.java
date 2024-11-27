package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import server.ResponseException;
import websocket.commands.*;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint { //need to extend Endpoint for websocket to work properly
    Session session; //this is a class from Spark? or from Glass fish?
    ServerMessageHandler messageHandler;

    public WebSocketFacade(String url, ServerMessageHandler messageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage messages = new Gson().fromJson(message, ServerMessage.class);
                    switch (messages.getServerMessageType()){
                        case NOTIFICATION -> messageHandler.notify(new Gson().fromJson(message, Notification.class));
                        case ERROR -> messageHandler.notify(new Gson().fromJson(message, Error.class));
                        case LOAD_GAME -> messageHandler.notify(new Gson().fromJson(message, LoadGame.class));
                    }
                } //this works with the on message in the websockethandler
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override //Endpoint requires this method, but you don't have to do anything
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    //Implement all the commands-------------

    //Connect - Used for a user to request to connect to a game as a player or observer
    public void connect(String auth, GameData game) throws ResponseException {
        try {
            Connect connect = new Connect(auth, game.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage()); //what should go here? should this be a good user response?
        }
    }

    public void makeMove(String auth, GameData game, ChessMove move) throws ResponseException {
        try {
            MakeMove makeMove = new MakeMove(auth, game.gameID(), move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMove));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String auth, GameData game) throws ResponseException {
        try{
            Leave leave = new Leave(auth, game.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(leave));
            this.session.close();
        } catch (IOException ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

}