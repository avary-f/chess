package dataaccess;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteGame(GameData game);
    void update(GameData game);
    void create(GameData game); //this will only have the name in it
    GameData get(GameData game); //like a read operation
    ArrayList<GameData> getAll();
}
