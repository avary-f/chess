package dataaccess;

public interface GameDAO {
    public void deleteGame(GameData game);
    public GameData update(GameData game);
    public GameData create(GameData game); //this will only have the name in it
    public GameData get(GameData game); //like a read operation
}
