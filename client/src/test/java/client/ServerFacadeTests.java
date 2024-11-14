package client;

import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverF;
    private static String goodAuth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:8080";
        serverF = new ServerFacade(url);
        serverF.deleteAll();
        RegisterResult result = serverF.register(new RegisterRequest("root", "pass", "email"));
        goodAuth = result.authToken();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerSuccess() {
        RegisterResult result = serverF.register(new RegisterRequest("testing", "testpass", "email"));
        Assertions.assertEquals("testing", result.username());
    }

    @Test
    public void registerFailureDuplicateUsername() {
        Assertions.assertThrows(ResponseException.class, () -> serverF.register(new RegisterRequest("testing", "testpass", "email")));
    }

    @Test
    public void loginSuccess() {
        LoginResult result = serverF.login(new LoginRequest("testing", "testpass"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void loginInvalidUsername() {
        Assertions.assertThrows(ResponseException.class, () -> serverF.login(new LoginRequest("bad username", "testpass")));
    }

    @Test
    public void logoutSuccess() {
        serverF.logout(new LogoutRequest(goodAuth));
        Assertions.assertThrows(ResponseException.class, () -> serverF.createGame(new CreateRequest("testing"), goodAuth));
        goodAuth = serverF.login(new LoginRequest("root", "pass")).authToken();
    }

    @Test
    public void logoutInvalidAuthToken() {
        Assertions.assertThrows(ResponseException.class, () -> serverF.logout(new LogoutRequest("badauthtoken")));
    }

    @Test
    public void listGameSuccess() {
        serverF.createGame(new CreateRequest("testing"), goodAuth);
        ListResult result = serverF.listGames(new ListRequest(goodAuth));
        Assertions.assertNotNull(result.games());
    }

    @Test
    public void listInvalidAuthToken() {
        Assertions.assertThrows(ResponseException.class, () -> serverF.listGames(new ListRequest("bad auth")));
    }

    @Test
    public void createGameSuccess() {
        int originalSize = serverF.listGames(new ListRequest(goodAuth)).games().size();
        serverF.createGame(new CreateRequest("testingAnotherGame"), goodAuth);
        Assertions.assertEquals(serverF.listGames(new ListRequest(goodAuth)).games().size(), originalSize + 1);
    }

    @Test
    public void createInvalidAuthToken() {
        Assertions.assertThrows(ResponseException.class, () -> serverF.createGame(new CreateRequest("testingAnotherGame"), "bad auth"));
    }

    @Test
    public void joinGameSuccess() {
        CreateResult result = serverF.createGame(new CreateRequest("testingAnotherGameAgain"), goodAuth);
        Assertions.assertDoesNotThrow(() -> serverF.joinGame(new JoinRequest(goodAuth, "WHITE", result.gameID())));
    }

    @Test
    public void joinInvalidAuthToken() {
        Assertions.assertThrows(ResponseException.class, () -> serverF.joinGame(new JoinRequest("bad auth", "WHITE", 123)));
    }
}
