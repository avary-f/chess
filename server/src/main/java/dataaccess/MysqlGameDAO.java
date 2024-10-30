package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MysqlGameDAO implements GameDAO{
    DatabaseManager databaseManager;

    public MysqlGameDAO(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

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
