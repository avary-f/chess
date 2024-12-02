package request;

public class EndGameRequest extends UnjoinRequest {
    public EndGameRequest(String authToken, int gameID) {
        super(authToken, gameID);
    }
}
