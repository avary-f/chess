package request;

import model.AuthData;

public record JoinRequest(AuthData auth, String playerColor, int gameID) {
}
