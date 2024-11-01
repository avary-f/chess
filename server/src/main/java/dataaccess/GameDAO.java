package dataaccess;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteGame(GameData game);
    void clearAll();
    void create(GameData game);
    GameData get(GameData game);
    boolean nameExists(GameData game);
    ArrayList<GameData> getAll();
}
