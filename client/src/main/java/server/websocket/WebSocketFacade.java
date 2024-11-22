package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import server.ResponseException;
import websocket.commands.*;
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
                    messageHandler.notify(messages);
                }
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
            Connect connect = new Connect(UserGameCommand.CommandType.CONNECT, auth, game.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage()); //what should go here? should this be a good user response?
        }
    }

//    public void leavePetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

}