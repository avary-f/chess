package websocket.messages;

import model.GameData;

public class LoadGameHighlight extends ServerMessage{
    private final String square;
    private final GameData game;

    public LoadGameHighlight(GameData game, String message, String square) {
        super(ServerMessageType.LOAD_GAME_HIGHLIGHT);
        this.message = message;
        this.square = square;
        this.game = game;
    }

    public String getHighlightSquare(){
        return square;
    }

    public GameData game(){
        return game;
    }
}
