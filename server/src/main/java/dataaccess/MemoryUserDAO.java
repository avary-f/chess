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
    public void create(UserData user) throws DataAccessException {
        if(users.get(user.username()) != null){
            throw new DataAccessException("already taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public void delete(UserData user) {
        users.remove(user.username());
    }

    @Override
    public void clearAll(){
        for(UserData user: users.values()){
            delete(user);
        }
    }

    @Override
    public void checkValidUser(UserData user) throws DataAccessException{
        if(user == null){
            throw new DataAccessException("unauthorized");
        }
    }

    @Override
    public void checkPasswordsEqual(UserData user1, UserData user2) throws DataAccessException {
        if(!(user1.password().equals(user2.password()))){
            throw new DataAccessException("unauthorized");
        }
    }
}
