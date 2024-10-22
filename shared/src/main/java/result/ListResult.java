package result;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public record ListResult(ArrayList<GameData> games) {
    @Override
    public String toString() {
        return "ListResult{" +
                "gameList=" + games +
                '}';
    }
}
