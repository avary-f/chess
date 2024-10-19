package dataaccess;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteGame(GameData game);
    void clearAll();
    GameData updatePlayer(String username, String color, GameData game) throws DataAccessException;
    void create(GameData game); //this will only have the name in it
    GameData getID(GameData game); //like a read operation
    void getName(GameData game) throws DataAccessException;
    ArrayList<GameData> getAll();
}
