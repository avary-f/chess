package client;

import server.websocket.ServerMessageHandler;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

import server.websocket.WebSocketFacade;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.GREEN;

public abstract class ChessClient implements ServerMessageHandler{
    private String clientName = null;
    public final ServerFacade server;
    public WebSocketFacade ws;
    private final String serverUrl;
    private State state;
    private String auth;
    private GameData game;
    private String teamColor;
    private BoardReader boardReader;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
        this.serverUrl = serverUrl;
    }

    public void printPrompt() {
        if(state.equals(State.LOGGEDOUT)){ //if they are signed out
            System.out.print("\n" + RESET + "[LOGGED_OUT] >>> " + GREEN);
        }
        else{ //if they are signed in
            System.out.print("\n" + RESET + "[" + getClientName().toUpperCase() + "] >>> " + GREEN);
        }

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

    public String getClientName(){return clientName;}

    public void notify(Notification message) {
        System.out.print(RESET_BG_COLOR);
        System.out.println(BLUE + message.getMessage());
        printPrompt();
    }
    public void notify(LoadGame message) {
        System.out.println("testing");
        System.out.print(RESET_BG_COLOR);
        setGame(message.game());
        boardReader = new BoardReader(getGame(), getTeamColor());
        boardReader.drawChessBoard();
        System.out.println(BLUE + message.getMessage());
        printPrompt();
    }
    public void notify(Error message) {
        System.out.print(RESET_BG_COLOR);
        System.out.println(RED + message.getMessage());
        printPrompt();
    }

    protected BoardReader getBoardReader(){
        return boardReader;
    }
    protected void updateBoardReader(BoardReader boardReader){
        this.boardReader = boardReader;
    }


}

