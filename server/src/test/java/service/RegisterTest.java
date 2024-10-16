package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import request.RegisterRequest;
import result.RegisterResult;
import org.junit.jupiter.api.*;

public class RegisterTest {

    private UserService testing;

    @BeforeEach
    public void setUp(){
        testing = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    }
    /*
    Testing a successful request to register a user
     */
    @Test
    public void testRegisterSucces() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("avaryef", "testing", "@gmail.com");
        RegisterResult result = testing.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("avaryef", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testRegisterDuplicateUser() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("avaryef", "testing", "@gmail.com");
        try{
            testing.register(request); //add the request that you just made
            RegisterResult result2 = testing.register(request); //should fail because you can't add it twice
            Assertions.fail("Expected DataAccessExpection to be thrown due to duplicate user");
        }
        catch(DataAccessException error){
            Assertions.assertEquals("already taken", error.getMessage());
        }
    }
}
