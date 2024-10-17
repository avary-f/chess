package dataaccess;
import model.UserData;

public interface UserDAO {
    UserData get(UserData user);
    void create(UserData user) throws DataAccessException;
    void delete(UserData user) throws DataAccessException;
    void clearAll();
    void checkValidUser(UserData user) throws DataAccessException;
    void checkPasswordsEqual(UserData user1, UserData user2) throws DataAccessException;

}
