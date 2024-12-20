package dataaccess;
import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteGame(GameData game);
    void clearAll();
    void create(GameData game);
    GameData get(GameData game);
    boolean nameExists(GameData game);
    void updatePlayer(GameData game, ChessGame.TeamColor playerColor, String username);
    void updateGame(GameData game);

    ArrayList<GameData> getAll();
}
