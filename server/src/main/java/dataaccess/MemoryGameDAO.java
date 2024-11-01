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
    public void create(GameData game) {
        games.put(game.gameID(), game); //might need to add more game data here?
    }

    @Override
    public GameData get(GameData game) {
        return games.get(game.gameID());
    }

    @Override
    public boolean nameExists(GameData game){
        for(GameData cur: games.values()){
            if(cur.gameName().equals(game.gameName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<GameData> getAll() {
        return new ArrayList<>(games.values());
    }
}
