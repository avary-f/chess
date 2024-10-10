package service;
import dataaccess.UserDAO;

public class UserService {
    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess){
        this.dataAccess = dataAccess;
    }
    public AuthData register(UserData user) {}
//    public AuthData login(UserData user) {}
//    public void logout(AuthData auth) {}
//    public void clearAllUsers(){}
}

