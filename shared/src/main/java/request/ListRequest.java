package request;

import model.AuthData;

public class ListRequest {
    private final AuthData auth;

    public ListRequest(String authToken) {
        auth = new AuthData(authToken, null);
    }
    public AuthData auth(){
        return auth;
    }
    @Override
    public String toString() {
        return "ListRequest{" +
                "auth=" + auth +
                '}';
    }
}
