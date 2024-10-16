package dataaccess;
import model.UserData;

public interface UserDAO {
    UserData get(UserData user) throws DataAccessException;
    void create(UserData user) throws DataAccessException;
    void delete(UserData user) throws DataAccessException;
    void clearAll();

}
