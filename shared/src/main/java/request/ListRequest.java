package request;

import model.AuthData;

public record ListRequest(AuthData auth) {
    @Override
    public String toString() {
        return "ListRequest{" +
                "auth=" + auth +
                '}';
    }
}
