package dataaccess;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void deleteGame(GameData game) {
        games.remove(game.gameID());
    }

    @Override
    public void clearAll() {
        games.clear();
    }

    @Override
    public GameData updatePlayer(String username, String color, GameData game) throws DataAccessException {
        GameData newGame;
        if(color.equals("WHITE")){ //player wants to be white
            if(games.get(game.gameID()).whiteUsername() != null){
                throw new DataAccessException("already taken");
            }
            newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }
        else{ //black
            if(games.get(game.gameID()).blackUsername() != null){
                throw new DataAccessException("already taken");
            }
            newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        games.remove(game.gameID());
        games.put(newGame.gameID(), newGame);
        return newGame;
    }

    @Override
    public void create(GameData game) {
        games.put(game.gameID(), game); //might need to add more game data here?
    }

    @Override
    public GameData getID(GameData game) {
        return games.get(game.gameID());
    }

    @Override
    public void getName(GameData game) throws DataAccessException {
        for(GameData cur: games.values()){
            if(cur.gameName().equals(game.gameName())){
                throw new DataAccessException("game already exists");
            }
        }
    }

    @Override
    public ArrayList<GameData> getAll() {
        return new ArrayList<>(games.values());
    }
}
