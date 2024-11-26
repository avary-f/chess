package request;

import chess.ChessMove;
import model.AuthData;

public class MoveRequest {
    private final AuthData auth;
    private final ChessMove move;
    private final int gameID;

    public MoveRequest(String authToken, int gameID,  ChessMove move) {
        auth = new AuthData(authToken, null);
        this.gameID = gameID;
        this.move = move;
    }
    public AuthData auth(){
        return auth;
    }
    public int gameID(){
        return gameID;
    }
    public ChessMove move(){
        return move;
    }
}
