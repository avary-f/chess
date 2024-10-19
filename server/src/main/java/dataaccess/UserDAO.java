package dataaccess;
import model.UserData;

public interface UserDAO {
    UserData get(UserData user);
    void create(UserData user);
    void clearAll();

}
