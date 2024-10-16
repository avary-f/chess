package request;

public record JoinRequest(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
    @Override
    public String toString() {
        return "GameRequest{" +
                "gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                '}';
    }
}
