package dataaccess;
import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void delete(AuthData data) throws Exception;
    void clearAll() throws DataAccessException;
    AuthData get(AuthData data);
    AuthData create(UserData data); //does this need to be associated with a user?
}
