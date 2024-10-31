package dataAccess;

import dataaccess.GameDAO;
import dataaccess.MysqlDAO;
import model.GameData;

import java.util.ArrayList;

public class GameTableTests extends MysqlDAO implements GameDAO {
    @Override
    public void deleteGame(GameData game) {

    }

    @Override
    public void clearAll() {

    }

    @Override
    public void create(GameData game) {

    }

    @Override
    public GameData getID(GameData game) {
        return null;
    }

    @Override
    public boolean getName(GameData game) {
        return false;
    }

    @Override
    public ArrayList<GameData> getAll() {
        return null;
    }
}
