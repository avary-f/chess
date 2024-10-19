package dataaccess;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData get(UserData user) {
        return users.get(user.username());
    }

    @Override
    public void create(UserData user){
        users.put(user.username(), user);
    }

    @Override
    public void clearAll(){
        users.clear();
    }
}
