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
        for(AuthData auth: list.values()){
            delete(auth);
        }
    }

    @Override
    public void checkAuthTokenValid(AuthData auth) throws DataAccessException{
        if(auth == null || list.get(auth.authToken()) == null){
            throw new DataAccessException("unauthorized");
        }
    }

    @Override
    public AuthData get(AuthData data) {
        return list.get(data.authToken());
    }

    @Override
    public String getUsername(AuthData auth) {
        AuthData data = list.get(auth.authToken());
        return data.username();

    }

    @Override
    public AuthData create(UserData data) {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, data.username());
        list.put(authToken, auth);
        return auth;
    }
}
