package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage{
    private GameData game;

    public LoadGame(GameData game, String message) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.message = message;
    }
}
