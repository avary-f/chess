package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> list = new HashMap<>(); //memory of authData objects

    @Override
    public void delete(AuthData data) {
        list.remove(data.authToken());
    }

    @Override
    public void clearAll(){
        list.clear();
    }

    @Override
    public AuthData get(AuthData data) {
        return list.get(data.authToken());
    }

    @Override
    public AuthData create(UserData data) {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, data.username());
        list.put(authToken, auth);
        return auth;
    }
}
