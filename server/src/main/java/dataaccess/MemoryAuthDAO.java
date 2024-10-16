package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> list = new HashMap<>(); //memory of authData objects

    @Override
    public void delete(AuthData data) {
        list.remove(data.username());
    }

    @Override
    public AuthData get(AuthData data) {
        return list.get(data.authToken());
    }
    @Override
    public AuthData create(UserData data) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), data.username());
        list.put(data.username(), auth);
        return auth;
    }
}
