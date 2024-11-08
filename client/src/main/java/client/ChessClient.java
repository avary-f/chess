package client;

//import client.websocket.NotificationHandler;
import com.sun.nio.sctp.NotificationHandler;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
//import client.websocket.WebSocketFacade;

public abstract class ChessClient implements ChessClientInterface {
    private String clientName = null;
    public final ServerFacade server;
    private final String serverUrl;
    private State state;
//    final NotificationHandler notificationHandler;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
//        this.notificationHandler = notificationHandler;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return performCmd(cmd);
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public boolean isLoggedIn(){
        return state == State.LOGGEDIN;
    }

    protected void setState(State s){
        state = s;
    }

    protected void setClientName(String clientName){
        this.clientName = clientName;
    }

    public abstract String help();

    public abstract String performCmd(String cmd);

    public String getServerUrl() {
        return serverUrl;
    }

//    public NotificationHandler getNotificationHandler() {
//        return notificationHandler;
//    }

    public String getClientName(){return clientName;}
}

