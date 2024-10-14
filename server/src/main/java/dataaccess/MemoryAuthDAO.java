package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> list = new HashMap<>(); //memory of authData objects
    //authTokens are connected to the user by the username

    @Override
    public void delete(AuthData data) {
        list.remove(data.username());
    }

    @Override
    public AuthData get(AuthData data) {
        return list.get(data.username());
    }

    @Override
    public AuthData create(UserData data) {
        AuthData auth = new AuthData("testing", data.username());
        list.put(data.username(), auth);
        return auth;
    }
}
