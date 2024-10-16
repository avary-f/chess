package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import org.junit.jupiter.api.*;

public class LoginTest {

    private UserService testing;

    @BeforeEach
    public void setUp() throws DataAccessException {
        testing = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        RegisterRequest request = new RegisterRequest("avaryef", "testing", "@gmail.com");
        testing.register(request);
    }
    @Test
    public void testLoginSuccess() throws DataAccessException{
        LoginRequest request = new LoginRequest("avaryef", "testing");
        LoginResult result = testing.login(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("avaryef", result.username());
        Assertions.assertNotNull(result.authToken());
    }
    @Test
    public void testDuplicateLoginSuccess() throws DataAccessException{
        LoginRequest request = new LoginRequest("avaryef", "testing");
        LoginResult result = testing.login(request);
        LoginResult result2 = testing.login(request); //attempt to log in a second time

        Assertions.assertNotNull(result2);
        Assertions.assertEquals("avaryef", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testLoginInvalidPassword() throws DataAccessException{
        LoginRequest request = new LoginRequest("avaryef", "test");
        try{
            testing.login(request); //should fail because you can't add it twice
            Assertions.fail("Expected DataAccessExpection to be thrown due to invalid password");
        }
        catch(DataAccessException error){
            Assertions.assertEquals("unauthorized", error.getMessage());
        }
    }
    @Test
    public void testLoginInvalidUsername() throws DataAccessException{
        LoginRequest request = new LoginRequest("ava", "testing");
        try{
            testing.login(request); //should fail because you can't add it twice
            Assertions.fail("Expected DataAccessExpection to be thrown due to invalid username");
        }
        catch(DataAccessException error){
            Assertions.assertEquals("unauthorized", error.getMessage());
        }
    }
}
