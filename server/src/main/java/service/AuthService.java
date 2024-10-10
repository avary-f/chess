package service;

import dataaccess.AuthDAO;

public class AuthService {
    private final AuthDAO dataAccess;

    public AuthService(AuthDAO dataAccess){
        this.dataAccess = dataAccess;
    }
    public AuthData createAuth(){} //should this take in a user?
//    public void delete(AuthData data){}
//    public void clearAllAuths(){}
//    public AuthData verifyAuth(AuthData data){}
}
