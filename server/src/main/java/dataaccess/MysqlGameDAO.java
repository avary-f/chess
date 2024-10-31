package dataaccess;
import model.GameData;

import java.util.ArrayList;

public class MysqlGameDAO extends MysqlDAO implements GameDAO{

    @Override
    public void deleteGame(GameData game) {
        String statement = "DELETE FROM games WHERE gameID = ?";
        execute(statement, game.gameID());
    }

    @Override
    public void clearAll() {
        String statement = "TRUNCATE games";
        execute(statement);
    }

    @Override
    public void create(GameData game) {
        String statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        execute(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getID(GameData game) {
        String statement = "SELECT gameID FROM games WHERE gameName = ?";
        Integer gameID = (Integer) execute(statement, game.gameName());
        if(gameID != null){ //does not exist in DB
            return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        }
        return null;
    }

    @Override
    public boolean nameExists(GameData game) {
        String statement = "SELECT * FROM games WHERE gameName = ?";
        Object result = execute(statement, game.gameName());
        return result != null; //if it doesn't exist, return false
    }

    @Override
    public ArrayList<GameData> getAll() {
        String statement = "SELECT game FROM games";
        Object result = execute(statement);
        if(result == null){
            return new ArrayList<>();
        }
        return (ArrayList<GameData>) result;
    }
}
