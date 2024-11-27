package dataaccess;
import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MysqlGameDAO extends MysqlDAO implements GameDAO{

    @Override
    public void deleteGame(GameData game) {
        if(get(game) == null){ //authToken does not exist in the DB
            throw new DataAccessException("bad request");
        }
        String statement = "DELETE FROM games WHERE id = ?";
        execute(statement, game.gameID());
    }

    @Override
    public void clearAll() {
        String statement = "TRUNCATE games";
        execute(statement);
    }

    @Override
    public void create(GameData game) {
        String statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        String gameJsonString = new Gson().toJson(game.game());
        execute(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameJsonString);
    }

    @Override
    public GameData get(GameData game) {
        String whiteUserStatement = "SELECT whiteUsername FROM games WHERE id = ?";
        String blackUserStatement = "SELECT blackUsername FROM games WHERE id = ?";
        String gameNameStatement = "SELECT gameName FROM games WHERE id = ?";
        String gameStatement = "SELECT game FROM games WHERE id = ?";
        String whiteUser = (String) execute(whiteUserStatement, game.gameID());
        String blackUser = (String) execute(blackUserStatement, game.gameID());
        String gameName = (String) execute(gameNameStatement, game.gameID());
        String jsonGame = (String) execute(gameStatement, game.gameID());
        ChessGame gsonGame = new Gson().fromJson(jsonGame, ChessGame.class);
        if(gameName != null){ //does not exist in DB
            return new GameData(game.gameID(), whiteUser, blackUser, gameName, gsonGame);
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
    public void updatePlayer(GameData game, ChessGame.TeamColor playerColor, String username) {
        String statement = playerColor.equals(ChessGame.TeamColor.WHITE) ? "UPDATE games SET whiteUsername = ? WHERE gameName = ?" :
                "UPDATE games SET blackUsername = ? WHERE gameName = ?";
        execute(statement, username, game.gameName());
    }

    @Override
    public void updateGame(GameData game) {
        String statement = "UPDATE games SET game = ? WHERE gameName = ?";
        String gameJsonString = new Gson().toJson(game.game());
        execute(statement, gameJsonString, game.gameName());
    }

    @Override
    public ArrayList<GameData> getAll() {
        String statement = "SELECT id FROM games";
        Object result = execute(statement);
        ArrayList<GameData> gameList = new ArrayList<>();
        if(result == null){
            return gameList; //return empty
        }
        List<Object> gameIDlist = (List<Object>) result;
        for(Object cur: gameIDlist){
            GameData curGame = get(new GameData((Integer) cur, null, null, null, null));
            gameList.add(curGame);
        }
        return gameList;
    }
}
