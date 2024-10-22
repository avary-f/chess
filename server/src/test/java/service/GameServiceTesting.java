package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;

import java.util.Random;
import request.*;
import result.CreateResult;
import result.JoinResult;
import result.ListResult;
import result.LoginResult;
import org.junit.jupiter.api.*;
import server.AlreadyTakenException;
import server.UnauthorizedException;

public class GameServiceTesting {

    private GameService serviceGame;
    private UserService serviceUser;
    private MemoryAuthDAO authDao = new MemoryAuthDAO();
    private MemoryGameDAO gameDao = new MemoryGameDAO();
    private MemoryUserDAO userDao = new MemoryUserDAO();
    private LoginResult resultLogin;
    private CreateResult resultCreate;
    private AuthData data;
    private JoinResult resultJoin;
    private Random random = new Random();

    public void createGames(String authToken, int count) throws Exception{
        for(int i = 0; i < count; i++){
            data = new AuthData(authToken, resultLogin.username());
            CreateRequest request = new CreateRequest( "TheGameName" + i);
            request.setAuthtoken(data.authToken());
            resultCreate = serviceGame.createGame(request);
        }
    }

    public void registerAndLogin(int numUsers)  throws Exception {
        for(int i = 0; i < numUsers; i++){
            String username = "user" + random.nextDouble(100);
            //Register User
            RegisterRequest registerRequest = new RegisterRequest(username, "testing", "@gmail.com");
            serviceUser.register(registerRequest);
            //Login User
            LoginRequest request = new LoginRequest(username, "testing");
            resultLogin = serviceUser.login(request);
        }
    }
    @BeforeEach
    public void setUp()  throws Exception {
        serviceGame = new GameService(userDao, authDao, gameDao);
        serviceUser = new UserService(userDao, authDao);

        registerAndLogin(1);
    }

    // Testing CreateGame
    @Test
    public void testCreateSuccess()  throws Exception {
        int numGames = 1;
        createGames(resultLogin.authToken(), numGames);
        Assertions.assertNotNull(resultCreate);
    }
    @Test
    public void testCreateGameTaken()  throws Exception {
        //1st Game Creation, Success
        createGames(resultLogin.authToken(), 1);
        //2nd Game Creation Attempt, Fail
        AlreadyTakenException exception = Assertions.assertThrows(AlreadyTakenException.class, () -> {
            createGames(resultLogin.authToken(), 1);
        });
        Assertions.assertEquals("game already exists", exception.getMessage());
    }
    @Test
    public void testCreateInvalidAuth()  throws Exception {
        //1st Game Creation, Success
        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> {
            createGames("invalidAuth", 1);
        });
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }
    // Testing ListGames
    @Test
    public void testListGamesSuccess()  throws Exception {
        int numGames = 5; //number of games you want to list
        createGames(resultLogin.authToken(), numGames);
        ListResult resultList = serviceGame.listGames(new ListRequest(data.authToken()));
        Assertions.assertEquals(resultList.gameList().size(), numGames);
    }
    @Test
    public void testListGamesInvalidAuth()  throws Exception {
        int numGames = 5; //number of games you want to list
        createGames(resultLogin.authToken(), numGames);
        AuthData invalidData = new AuthData("invalidAuth", resultLogin.username());
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            serviceGame.listGames(new ListRequest(invalidData.authToken()));
        });
    }

    @Test
    public void testJoinGameSuccessWhite()  throws Exception {
        createGames(resultLogin.authToken(), 1);
        int gameID = resultCreate.gameID();
        JoinRequest requestJoin = new JoinRequest(data.authToken(), "WHITE", gameID);
        resultJoin = serviceGame.joinGame(requestJoin);
        Assertions.assertEquals(resultJoin.gameID(), requestJoin.gameID());
        Assertions.assertEquals(resultJoin.whiteUser(), data.username()); // need to check what the username of the request was
    }
    @Test
    public void testJoinGameSuccessBlack()  throws Exception {
        createGames(resultLogin.authToken(), 1);
        int gameID = resultCreate.gameID();
        JoinRequest requestJoin = new JoinRequest(data.authToken(), "BLACK", gameID);
        resultJoin = serviceGame.joinGame(requestJoin);
        Assertions.assertEquals(resultJoin.gameID(), requestJoin.gameID());
        Assertions.assertEquals(resultJoin.blackUser(), data.username());
    }

    @Test
    public void testJoinInvalidAuth()  throws Exception {
        createGames(resultLogin.authToken(), 1);
        int gameID = resultCreate.gameID();
        AuthData invalidData = new AuthData("invalidAuth", resultLogin.username());
        JoinRequest requestJoin = new JoinRequest(invalidData.authToken(), "BLACK", gameID);
        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> {
            serviceGame.joinGame(requestJoin);
        });
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    public void testJoinWhiteTaken()  throws Exception {
        createGames(resultLogin.authToken(), 1);
        int gameID = resultCreate.gameID();
        JoinRequest requestJoin = new JoinRequest(data.authToken(), "WHITE", gameID);
        serviceGame.joinGame(requestJoin);
        registerAndLogin(1);
        JoinRequest requestJoin2 = new JoinRequest(data.authToken(), "WHITE", gameID);
        AlreadyTakenException exception = Assertions.assertThrows(AlreadyTakenException.class, () -> {
            serviceGame.joinGame(requestJoin2);
        });
        Assertions.assertEquals("already taken", exception.getMessage());
    }
    @Test
    public void testJoinBlackTaken()  throws Exception {
        createGames(resultLogin.authToken(), 1);
        int gameID = resultCreate.gameID();
        JoinRequest requestJoin = new JoinRequest(data.authToken(), "BLACK", gameID);
        serviceGame.joinGame(requestJoin);
        registerAndLogin(1);
        JoinRequest requestJoin2 = new JoinRequest(data.authToken(), "BLACK", gameID);
        AlreadyTakenException exception = Assertions.assertThrows(AlreadyTakenException.class, () -> {
            serviceGame.joinGame(requestJoin2);
        });
        Assertions.assertEquals("already taken", exception.getMessage());
    }

    @Test
    public void clearDB()  throws Exception {
        registerAndLogin(20);
        createGames(resultLogin.authToken(), 10);
        serviceGame.clearAllGames();
        serviceUser.clearAllUserAuthData();
    }
}

