package dataaccess;
import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void delete(AuthData data);
    void clearAll();
    AuthData get(AuthData data);
    AuthData create(UserData data); //does this need to be associated with a user?
}
