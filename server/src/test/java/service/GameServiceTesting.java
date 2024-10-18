package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import request.*;
import result.CreateResult;
import result.ListResult;
import result.LoginResult;
import org.junit.jupiter.api.*;

public class GameServiceTesting {

    private GameService serviceGame;
    private UserService serviceUser;
    private MemoryAuthDAO authDao = new MemoryAuthDAO();
    private MemoryGameDAO gameDao = new MemoryGameDAO();
    private MemoryUserDAO userDao = new MemoryUserDAO();
    private LoginResult resultLogin;
    private CreateResult resultCreate;
    private AuthData data;

    public void createGames(String authToken, int count) throws DataAccessException {
        for(int i = 0; i < count; i++){
            data = new AuthData(authToken, resultLogin.username());
            CreateRequest request = new CreateRequest(data, "TheGameName" + i);
            resultCreate = serviceGame.createGame(request);
        }
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        serviceGame = new GameService(userDao, authDao, gameDao);
        serviceUser = new UserService(userDao, authDao);

        //Register User
        RegisterRequest registerRequest = new RegisterRequest("avaryef", "testing", "@gmail.com");
        serviceUser.register(registerRequest);
        //Login User
        LoginRequest request = new LoginRequest("avaryef", "testing");
        resultLogin = serviceUser.login(request);
    }

    // Testing CreateGame
    @Test
    public void testCreateSuccess() throws DataAccessException {
        int numGames = 1;
        createGames(resultLogin.authToken(), numGames);
        Assertions.assertNotNull(resultCreate);
    }
    @Test
    public void testCreateGameTaken() throws DataAccessException {
        //1st Game Creation, Success
        createGames(resultLogin.authToken(), 1);
        //2nd Game Creation Attempt, Fail
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            createGames(resultLogin.authToken(), 1);
        });
        Assertions.assertEquals("game already exists", exception.getMessage());
    }
    @Test
    public void testCreateInvalidAuth() throws DataAccessException {
        //1st Game Creation, Success
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            createGames("invalidAuth", 1);;
        });
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }
    // Testing ListGames
    @Test
    public void testListGamesSuccess() throws DataAccessException {
        int numGames = 5; //number of games you want to list
        createGames(resultLogin.authToken(), numGames);
        ListResult resultList = serviceGame.listGames(new ListRequest(data));
        Assertions.assertEquals(resultList.gameList().size(), numGames);
    }
    @Test
    public void testListGamesInvalidAuth() throws DataAccessException {
        int numGames = 5; //number of games you want to list
        createGames(resultLogin.authToken(), numGames);
        AuthData invalidData = new AuthData("invalidAuth", resultLogin.username());
        ListResult resultList = serviceGame.listGames(new ListRequest(invalidData));
        Assertions.assertEquals(resultList.gameList().size(), numGames);
    }
//
//    @Test
//    public void testLoginInvalidPassword(){
//        LoginRequest request = new LoginRequest("avaryef", "wrongPassword");
//        try {
//            service.login(request); // This should fail because of an invalid password
//            Assertions.fail("Expected DataAccessException to be thrown due to invalid password");
//        } catch (DataAccessException error) {
//            Assertions.assertEquals("unauthorized", error.getMessage());
//        }
//    }
//
//    @Test
//    public void testLoginInvalidUsername(){
//        LoginRequest request = new LoginRequest("wrongUsername", "testing");
//        try {
//            service.login(request); // This should fail because of an invalid username
//            Assertions.fail("Expected DataAccessException to be thrown due to invalid username");
//        } catch (DataAccessException error) {
//            Assertions.assertEquals("unauthorized", error.getMessage());
//        }
//    }
//
//    // Testing Logout
//    @Test
//    public void testLogoutSuccess() throws DataAccessException {
//        LoginRequest requestLogin = new LoginRequest("avaryef", "testing");
//        LoginResult result = service.login(requestLogin);
//        LogoutRequest requestLogout = new LogoutRequest(result.authToken());
//        Assertions.assertDoesNotThrow(() -> service.logout(requestLogout));
//    }
//
//    @Test
//    public void testLogoutInvalidAuth(){
//        LogoutRequest request = new LogoutRequest("invalidAuthtokenTest");
//        try {
//            service.logout(request); // This should fail due to an invalid auth token
//            Assertions.fail("Expected DataAccessException to be thrown due to invalid auth token");
//        } catch (DataAccessException error) {
//            Assertions.assertEquals("unauthorized", error.getMessage());
//        }
//    }
//
//    // Testing Register
//    @Test
//    public void testRegisterSuccess() throws DataAccessException {
//        RegisterRequest request = new RegisterRequest("newUser", "password", "newUser@gmail.com");
//        RegisterResult result = service.register(request);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals("newUser", result.username());
//        Assertions.assertNotNull(result.authToken());
//    }
//
//    @Test
//    public void testRegisterDuplicateUser(){
//        RegisterRequest request = new RegisterRequest("avaryef", "testing", "@gmail.com");
//        try {
//            service.register(request);
//            Assertions.fail("Expected DataAccessException to be thrown due to duplicate user");
//        } catch (DataAccessException error) {
//            Assertions.assertEquals("already taken", error.getMessage());
//        }
//    }
}

