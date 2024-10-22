package dataaccess;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteGame(GameData game);
    void clearAll();
    void create(GameData game);
    GameData getID(GameData game);
    boolean getName(GameData game);
    ArrayList<GameData> getAll();
}
