package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import request.CreateRequest;
import request.LoginRequest;
import request.RegisterRequest;
import request.LogoutRequest;
import result.CreateResult;
import result.LoginResult;
import result.RegisterResult;
import org.junit.jupiter.api.*;

public class GameServiceTesting {

    private GameService serviceGame;
    private UserService serviceUser;
    private MemoryAuthDAO authDao = new MemoryAuthDAO();
    private MemoryGameDAO gameDao = new MemoryGameDAO();
    private MemoryUserDAO userDao = new MemoryUserDAO();
    private LoginResult result;


    @BeforeEach
    public void setUp() throws DataAccessException {
        serviceGame = new GameService(userDao, authDao, gameDao);
        serviceUser = new UserService(userDao, authDao);

        //Register User
        RegisterRequest registerRequest = new RegisterRequest("avaryef", "testing", "@gmail.com");
        serviceUser.register(registerRequest);
        //Login User
        LoginRequest request = new LoginRequest("avaryef", "testing");
        result = serviceUser.login(request);
    }

    // Testing CreateGame
    @Test
    public void testCreateSuccess() throws DataAccessException {
        AuthData data = new AuthData(result.authToken(), result.username());
        CreateRequest request = new CreateRequest(data, "TheGameName");
        CreateResult result = serviceGame.createGame(request);

        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(result.gameID(), -1);
    }
    @Test
    public void testCreateGameTaken() throws DataAccessException {
        AuthData data = new AuthData(result.authToken(), result.username());
        //1st Game Creation, Success
        CreateRequest request = new CreateRequest(data, "TheGameName");
        CreateResult result = serviceGame.createGame(request);
        //2nd Game Creation Attempt, Fail
        CreateRequest request2 = new CreateRequest(data, "TheGameName");
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            serviceGame.createGame(request2);
        });
        Assertions.assertEquals("game already exists", exception.getMessage());
    }
    @Test
    public void testCreateInvalidAuth() throws DataAccessException {
        AuthData data = new AuthData(result.authToken(), result.username());
        //1st Game Creation, Success
        CreateRequest request = new CreateRequest(data, "TheGameName");
        CreateResult result = serviceGame.createGame(request);
        //2nd Game Creation Attempt, Fail
        CreateRequest request2 = new CreateRequest(data, "TheGameName");
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            serviceGame.createGame(request2);
        });
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

//    @Test
//    public void testDuplicateLoginSuccess() throws DataAccessException {
//        LoginRequest request = new LoginRequest("avaryef", "testing");
//        LoginResult result = service.login(request);
//        LoginResult result2 = service.login(request); // Attempt to log in a second time
//
//        Assertions.assertNotNull(result2);
//        Assertions.assertEquals("avaryef", result.username());
//        Assertions.assertNotNull(result.authToken());
//    }
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

