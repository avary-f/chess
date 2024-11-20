package client;

//import client.websocket.NotificationHandler;
import com.sun.nio.sctp.NotificationHandler;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
//import client.websocket.WebSocketFacade;

public abstract class ChessClient {
    private String clientName = null;
    public final ServerFacade server;
    private final String serverUrl;
    private State state;
    private String auth;
    private GameData game;
    private String teamColor;
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
            return performCmd(cmd, params);
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public boolean isLoggedIn(){
        return state == State.LOGGEDIN || state == State.GAMEPLAY;
    }

    public boolean isInGameplay(){
        return state == State.GAMEPLAY;
    }

    protected void setState(State s){
        state = s;
    }

    protected void setClientName(String clientName){
        this.clientName = clientName;
    }

    protected void setAuth(String auth){
        this.auth = auth;
    }

    protected String getAuth(){
        return auth;
    }

    protected void setGame(GameData game){
        this.game = game;
    }

    protected GameData getGame(){
        return game;
    }

    protected void setTeamColor(String teamColor){
        this.teamColor = teamColor;
    }

    protected String getTeamColor(){
        return teamColor;
    }

    public abstract String help();

    public abstract String performCmd(String cmd, String[] params);

    public String getServerUrl() {
        return serverUrl;
    }

    public String getClientName(){return clientName;}
}

