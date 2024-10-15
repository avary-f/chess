package service;
//import uuID?

import dataaccess.AuthDAO;

public class AuthService {
    private final AuthDAO dataAccess; //abstracting the DataAccess class and creating an object for it

    public AuthService(AuthDAO dataAccess){ //where should I be passing in what type of data access class?
        this.dataAccess = dataAccess;
    }
    public AuthData createAuth(){

    } //should this take in a user?
//    public void delete(model.AuthData data){}
//    public void clearAllAuths(){}
//    public model.AuthData verifyAuth(model.AuthData data){}
}
