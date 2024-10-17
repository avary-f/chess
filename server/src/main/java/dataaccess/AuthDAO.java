package dataaccess;
import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void delete(AuthData data);
    AuthData get(AuthData data);
    AuthData create(UserData data);
    void clearAll();
    void checkAuthTokenValid(AuthData data) throws DataAccessException;
}
