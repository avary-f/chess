package dataAccess;
import dataaccess.DataAccessException;
import dataaccess.MysqlAuthDAO;
import dataaccess.MysqlDAO;
import dataaccess.MysqlUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class UserTableTests {
    private static MysqlAuthDAO authDao = new MysqlAuthDAO();
    private static MysqlUserDAO userDao = new MysqlUserDAO();
    private ArrayList<UserData> auths = new ArrayList<>();
    public AuthData auth;
    public UserData user;
    private static MysqlDAO mysql;

    public void generateUsers(int n){
        for(int i = 0; i < n; i++){
            users.add(new UserData("user" + i, "password" + i, "gmail.com"));
        }
    }
    public void addAuthEntries(int n){
        generateUsers(n);
        user = users.getFirst();
        auth = authDao.create(user);
    }

    @BeforeAll
    public static void configure(){
        mysql = new MysqlDAO();
    }
    @AfterAll
    public static void deleteAuths(){
        authDao.clearAll();
    }

    @BeforeEach
    public void configureAuths(){
        addAuthEntries(10);
    }

    //CREATE AUTHS
    @Test
    public void createAuthsSuccess(){
        user = users.getFirst();
        auth = authDao.create(user);
        AuthData authUpdated = authDao.get(auth);
        Assertions.assertNotNull(authUpdated);
        Assertions.assertNotNull(authUpdated.username());
        Assertions.assertEquals(authUpdated.username(), auth.username());
    }
    @Test
    public void createAuthsInvalidUser(){
        UserData badUser = new UserData(null, null, null);
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            authDao.create(badUser);
        });
    }

    //DELETE AUTHS
    @Test
    public void deleteAuthsSuccess(){
        authDao.delete(auth);
        AuthData authUpdated = authDao.get(auth);
        Assertions.assertNull(authUpdated);
    }
    @Test
    public void deleteAuthsInvalidAuth(){
        AuthData badAuth = new AuthData("invalidAuthToken", users.getFirst().username());
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDao.delete(badAuth);
        });
    }

    //GET AUTHS
    @Test
    public void getAuthsSuccess(){
        AuthData authUpdated = authDao.get(auth);
        Assertions.assertNotNull(authUpdated.authToken());
    }
    @Test
    public void getAuthsInvalidAuth(){
        AuthData badAuth = new AuthData("invalidAuthToken", users.getFirst().username());
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDao.delete(badAuth);
        });
    }



    @Test
    public void clearTableSuccess(){
        authDao.clearAll();
        Assertions.assertTrue(authDao.isEmpty());
    }
}

