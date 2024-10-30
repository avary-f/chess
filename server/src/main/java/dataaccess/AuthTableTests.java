package dataaccess;
import model.AuthData;
import model.UserData;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTableTests {
    private MysqlAuthDAO authDao = new MysqlAuthDAO();
    private ArrayList<UserData> users = new ArrayList<>();
    private AuthData auth;
    private UserData user;
    private static MysqlDAO mysql;

    public void generateUsers(int n){
        for(int i = 0; i < n; i++){
           users.add(new UserData("user" + i, "password" + i, "gmail.com"));
           System.out.println("added user");
        }
    }

    @BeforeAll
    public static void configure(){
        mysql = new MysqlDAO();
    }

    @Test
    @Order(1)
    public void createOneUserSuccess(){
        generateUsers(1);
        user = users.getFirst();
        auth = authDao.create(user);
        AuthData authUpdated = authDao.get(auth);
        Assertions.assertNotNull(authUpdated);
        Assertions.assertNotNull(authUpdated.username());
        Assertions.assertEquals(authUpdated.username(), auth.username());
    }

    @Test
    @Order(2)
    public void clearTableSuccess(){
        authDao.clearAll();
        auth = authDao.get(auth);
        Assertions.assertNull(auth);
        Assertions.assertTrue(authDao.isEmpty());
    }
}
