package request;

public class EndGameRequest extends UnjoinRequest {
    private boolean isResigning;

    public EndGameRequest(String authToken, int gameID, boolean isResigning) {
        super(authToken, gameID);
        this.isResigning = isResigning;

    }

    public boolean isResigning(){
        return isResigning;
    }
}
