package dataaccess;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class UserTableTests {
    private static MysqlUserDAO userDao = new MysqlUserDAO();
    private final ArrayList<UserData> users = new ArrayList<>();
    public UserData user;
    private static MysqlDAO mysql;

    public void generateUsers(int n){
        for(int i = 0; i < n; i++){
            users.add(new UserData("user" + i, "password" + i, "gmail.com"));
        }
    }

    public void addUserEntries(int n){
        generateUsers(n);
        for (UserData userData : users) {
            userDao.create(userData);
        }
        user = users.getFirst();
    }

//    public void assertSameUser(UserData userUpdated, UserData user){
//        Assertions.assertEquals(userUpdated.username(), user.username());
//        Assertions.assertEquals(userUpdated.password(), user.password());
//        Assertions.assertEquals(userUpdated.email(), user.email());
//    }

    @BeforeAll
    public static void configure(){
        mysql = new MysqlDAO();
    }
    @AfterAll
    public static void deleteUsers(){
        userDao.clearAll();
    }

    @BeforeEach
    public void configureUser(){
        deleteUsers();
        addUserEntries(10);
    }

    //CREATE USERS
    @Test
    public void createUsersSuccess(){
        UserData newUser = new UserData("root", "pass", "@gmail");
        userDao.create(newUser);
        UserData userUpdated = userDao.get(newUser);
        Assertions.assertNotNull(userUpdated);
        Assertions.assertNotNull(userUpdated.username());
        Assertions.assertEquals(userUpdated, newUser);
    }
    @Test
    public void createUserInvalidUser(){ //should not allow you to make a user with a null username
        UserData badUser = new UserData(null, null, null);
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> {
            userDao.create(badUser);
        });
    }

    //GET USERS
    @Test
    public void getUserSuccess(){
        UserData userUpdated = userDao.get(user);
        Assertions.assertNotNull(userUpdated);
        Assertions.assertEquals(userUpdated, user);

    }
    @Test
    public void getUserInvalidUser(){
        UserData badUser = new UserData("badUserName", null, null);
        Assertions.assertNull(userDao.get(badUser));
    }


    //CLEAR TABLE
    @Test
    public void clearUsersTableSuccess(){
        userDao.clearAll();
        Assertions.assertTrue(userDao.isEmpty());
    }
}

