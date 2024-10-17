package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import org.junit.jupiter.api.*;

public class LogoutTest {

    private UserService testing;

    @BeforeEach
    public void setUp() throws DataAccessException {
        //Register the user
        testing = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        RegisterRequest requestRegister = new RegisterRequest("avaryef", "testing", "@gmail.com");
        testing.register(requestRegister);
    }
    @Test
    public void testLogoutSuccess() throws DataAccessException{
        //Login the user
        LoginRequest requestLogin = new LoginRequest("avaryef", "testing");
        LoginResult resultLogin = testing.login(requestLogin);

        Assertions.assertNotNull(resultLogin);
        Assertions.assertNotNull(resultLogin.authToken());

        LogoutRequest request = new LogoutRequest(resultLogin.authToken());

        Assertions.assertDoesNotThrow(() -> {testing.logout(request);}, "Function should not throw DataAccessException");
    }
    @Test
    public void testLogoutInvalidAuth() throws DataAccessException{
        LogoutRequest request = new LogoutRequest("invalidAuthtokenTest");
        try{
            testing.logout(request); //should fail because you can't add it twice
            Assertions.fail("Expected DataAccessExpection to be thrown due to invalid authtoken");
        }
        catch(DataAccessException error){
            Assertions.assertEquals("unauthorized", error.getMessage());
        }
    }
}
