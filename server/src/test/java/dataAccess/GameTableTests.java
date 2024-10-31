package dataAccess;

import chess.ChessBoard;
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
    public UserData user1;
    public UserData user2;
    public GameData game;
    private static MysqlDAO mysql;

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

    //CREATE USERS
    @Test
    public void createUsersSuccess(){
        UserData newUser = new UserData("root", "pass", "@gmail");
        userDao.create(newUser);
        UserData userUpdated = userDao.get(newUser);
        Assertions.assertNotNull(userUpdated);
        Assertions.assertNotNull(userUpdated.username());
        assertSameUser(userUpdated, newUser);
    }
    @Test
    public void createUserInvalidUser(){ //should not allow you to make a user with a null username
        UserData badUser = new UserData(null, null, null);
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            userDao.create(badUser);
        });
    }

    //GET USERS
    @Test
    public void getUserByUserSuccess(){
        UserData userUpdated = userDao.get(user);
        Assertions.assertNotNull(userUpdated);
        assertSameUser(userUpdated, user);

    }
    @Test
    public void getUserByUsernameSuccess(){
        UserData userUpdated = userDao.get(user);
        Assertions.assertNotNull(userUpdated);
        assertSameUser(userUpdated, user);

    }
    @Test
    public void getUserInvalidUser(){
        UserData badUser = new UserData("badUserName", null, null);
        Assertions.assertNull(userDao.get(badUser));
    }


    //CLEAR TABLE
    @Test
    public void clearUsersTableSuccess(){
        userDao.clearAll();
        Assertions.assertTrue(userDao.isEmpty());
    }
}
