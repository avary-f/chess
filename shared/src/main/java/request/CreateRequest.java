package request;

import model.AuthData;

public record CreateRequest(AuthData auth, String gameName) {
    @Override
    public String toString() {
        return "CreateRequest{" +
                "auth=" + auth +
                ", gameName='" + gameName + '\'' +
                '}';
    }
}
