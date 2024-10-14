package service;
import dataaccess.UserDAO;

public class UserService {
    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess){
        this.dataAccess = dataAccess;
    }
    public AuthData register(UserData user) {}
//    public model.AuthData login(model.UserData user) {}
//    public void logout(model.AuthData auth) {}
//    public void clearAllUsers(){}
}

