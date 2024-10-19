package dataaccess;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteGame(GameData game);
    void clearAll();
    GameData updatePlayer(GameData game, GameData newGame);
    void create(GameData game);
    GameData getID(GameData game);
    void getName(GameData game);
    ArrayList<GameData> getAll();
}
