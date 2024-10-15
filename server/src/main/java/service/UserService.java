package service;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.*;

public class UserService {
    private final UserDAO dataAccessUser;
    private final AuthDAO dataAccessAuth;

    public UserService(UserDAO dataAccess){
        this.dataAccessUser = dataAccessUser;
    }
    public AuthData register(UserData user) {}
//    public AuthData login(UserData user) {
//      validateAuthToken();
//    }
//    public void logout(AuthData auth) {}
//    public void clearAllUsers(){}
}

