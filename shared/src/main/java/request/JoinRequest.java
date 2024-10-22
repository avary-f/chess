package request;

import model.AuthData;

public class JoinRequest {
    private final AuthData auth;
    private final String playerColor;
    private final int gameID;

    public JoinRequest(String authToken, String playerColor, int gameID) {
        auth = new AuthData(authToken, null);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }
    public AuthData auth(){
        return auth;
    }
    public int gameID(){
        return gameID;
    }
    public String playerColor(){
        return playerColor;
    }
}
