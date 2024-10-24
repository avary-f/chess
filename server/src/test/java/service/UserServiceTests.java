package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import request.LoginRequest;
import request.RegisterRequest;
import request.LogoutRequest;
import result.LoginResult;
import result.RegisterResult;
import org.junit.jupiter.api.*;

public class UserServiceTests {

    private UserService service;

    @BeforeEach
    public void setUp() throws Exception {
        service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        RegisterRequest registerRequest = new RegisterRequest("avaryef", "testing", "@gmail.com");
        service.register(registerRequest);
    }

    // Testing Login
    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("avaryef", "testing");
        LoginResult result = service.login(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("avaryef", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testDuplicateLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("avaryef", "testing");
        LoginResult result = service.login(request);
        LoginResult result2 = service.login(request); // Attempt to log in a second time

        Assertions.assertNotNull(result2);
        Assertions.assertEquals("avaryef", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testLoginInvalidPassword(){
        LoginRequest request = new LoginRequest("avaryef", "wrongPassword");
        try {
            service.login(request); // This should fail because of an invalid password
            Assertions.fail("Expected DataAccessException to be thrown due to invalid password");
        } catch (Exception error) {
            Assertions.assertEquals("Error: unauthorized", error.getMessage());
        }
    }

    @Test
    public void testLoginInvalidUsername(){
        LoginRequest request = new LoginRequest("wrongUsername", "testing");
        try {
            service.login(request); // This should fail because of an invalid username
            Assertions.fail("Expected DataAccessException to be thrown due to invalid username");
        } catch (Exception error) {
            Assertions.assertEquals("Error: unauthorized", error.getMessage());
        }
    }

    // Testing Logout
    @Test
    public void testLogoutSuccess() throws Exception {
        LoginRequest requestLogin = new LoginRequest("avaryef", "testing");
        LoginResult result = service.login(requestLogin);
        LogoutRequest requestLogout = new LogoutRequest(result.authToken());
        Assertions.assertDoesNotThrow(() -> service.logout(requestLogout));
    }

    @Test
    public void testLogoutInvalidAuth(){
        LogoutRequest request = new LogoutRequest("invalidAuthtokenTest");
        try {
            service.logout(request); // This should fail due to an invalid auth token
            Assertions.fail("Expected DataAccessException to be thrown due to invalid auth token");
        } catch (Exception error) {
            Assertions.assertEquals("Error: unauthorized", error.getMessage());
        }
    }

    // Testing Register
    @Test
    public void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("newUser", "password", "newUser@gmail.com");
        RegisterResult result = service.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("newUser", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testRegisterDuplicateUser(){
        RegisterRequest request = new RegisterRequest("avaryef", "testing", "@gmail.com");
        try {
            service.register(request);
            Assertions.fail("Expected DataAccessException to be thrown due to duplicate user");
        } catch (Exception error) {
            Assertions.assertEquals("Error: already taken", error.getMessage());
        }
    }
}

