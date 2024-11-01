package dataAccess;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MysqlDAO;
import dataaccess.MysqlGameDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class GameTableTests  {
    private static final MysqlGameDAO gameDAO = new MysqlGameDAO();
    private final ArrayList<UserData> users = new ArrayList<>();
    private final ArrayList<GameData> games = new ArrayList<>();
    private final ArrayList<ChessGame> chessBoardGames = new ArrayList<>();
    private static MysqlDAO mysql;
    private static GameData game = new GameData(123, "userW", "userB",
            "testGame", new ChessGame());
    private GameData badGame = new GameData(-1, null, null, null, null);

    public void generateUsers(int n){
        for(int i = 0; i < n * 2; i++){
            users.add(new UserData("user" + i, "password" + i, "gmail.com"));
        }
    }

    public void generateChessBoardGames(int n){
        for(int i = 0; i < n; i ++){
            chessBoardGames.add(new ChessGame());
        }
    }

    public void createGames(int n){
        generateChessBoardGames(n);
        generateUsers(n);
        for(int i  = 0; i < n; i++){
            games.add(new GameData(i, users.get(i).username(), users.get(i+2).username(),
                    "gameName" + i, chessBoardGames.get(i)));
        }
    }

    @BeforeAll
    public static void configure(){
        deleteGames();
        mysql = new MysqlDAO();
    }
    @AfterAll
    public static void deleteGames(){
        gameDAO.clearAll();
    }

    @BeforeEach
    public void configureGame(){
        deleteGames();
        createGames(5);

    }

    //CREATE GAMES
    @Test
    public void createGameSuccess(){
        game = new GameData(123, "userW", "userB",
                "testGame", new ChessGame());
        gameDAO.create(game);
        GameData gameUpdated = gameDAO.get(game);
        Assertions.assertEquals(game, gameUpdated);
    }
    @Test
    public void createUserInvalidGame(){ //should not allow you to make a game with a null gameName
        GameData badGame = new GameData(321, "userW", "userB",
                null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.create(badGame);
        });
    }

    //GET GAME
    @Test
    public void getGameByGameSuccess(){
        gameDAO.create(game);
        GameData newGame = new GameData(123, null, null, null, null);
        GameData gameUpdated = gameDAO.get(newGame);
        Assertions.assertEquals(game, gameUpdated);

    }
    @Test
    public void getGameInvalidGameID(){
        Assertions.assertNull(gameDAO.get(badGame));
    }

    //DELETE GAMES
    @Test
    public void deleteGameSuccess(){
        if(gameDAO.get(game) == null){
            gameDAO.create(game);
        }
        gameDAO.deleteGame(game);
        Assertions.assertNull(gameDAO.get(game));
        gameDAO.create(game); //add it back for later uses
    }
    @Test
    public void deleteGameInvalidGame(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.deleteGame(badGame);
        });
    }

    //LIST GAMES
    @Test
    public void listGamesSuccess(){
        gameDAO.clearAll();
        GameData game1 = games.getFirst();
        GameData game2 = games.getLast();
        gameDAO.create(game1);
        gameDAO.create(game2);
        ArrayList<GameData> listOriginal = new ArrayList<>();
        listOriginal.add(game1);
        listOriginal.add(game2);
        ArrayList<GameData> list = gameDAO.getAll();
        Assertions.assertEquals(list, listOriginal);
    }

    //CLEAR TABLE
    @Test
    public void clearUsersTableSuccess(){
        gameDAO.clearAll();
        Assertions.assertTrue(gameDAO.isEmpty());
    }
}
