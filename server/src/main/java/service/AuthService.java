package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final AuthDAO dataAccessAuth;

    public AuthService(AuthDAO dataAccessAuth){
        this.dataAccessAuth = dataAccessAuth;
    }
    public void checkAuthTokenValid(AuthData auth) throws DataAccessException {
        if(auth == null || dataAccessAuth.get(auth) == null){
            throw new DataAccessException("unauthorized");
        }
    }
    public String getUsername(AuthData auth) {
        AuthData data = dataAccessAuth.get(auth);
        return data.username();

    }
}
