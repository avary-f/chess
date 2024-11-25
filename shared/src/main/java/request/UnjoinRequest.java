package request;

import model.AuthData;

public class UnjoinRequest {
    private final AuthData auth;
    private final int gameID;

    public UnjoinRequest(String authToken, int gameID) {
        auth = new AuthData(authToken, null);
        this.gameID = gameID;
    }
    public AuthData auth(){
        return auth;
    }
    public int gameID(){
        return gameID;
    }

}
