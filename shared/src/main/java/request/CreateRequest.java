package request;

import model.AuthData;

public class CreateRequest {
    private AuthData auth;
    private final String gameName;

    public CreateRequest(String gameName) {
        this.gameName = gameName;
    }
    public void setAuthtoken(String authToken){
        auth = new AuthData(authToken, null);
    }
    public AuthData auth(){
        return auth;
    }
    public String gameName(){
        return gameName;
    }
}

