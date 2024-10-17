package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;
import result.*;
import request.*;

public class UserService {
    private final UserDAO dataAccessUser;
    private final AuthDAO dataAccessAuth;

    public UserService(UserDAO dataAccessUser, AuthDAO dataAccessAuth){
        this.dataAccessAuth = dataAccessAuth;
        this.dataAccessUser = dataAccessUser;
    }
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData user = new UserData(request.username(), request.password(), request.email());
        dataAccessUser.create(user);
        AuthData auth = dataAccessAuth.create(user);
        return new RegisterResult(request.username(), auth.authToken());
    }
    public LoginResult login(LoginRequest request) throws DataAccessException{
        UserData userRequest = new UserData(request.username(), request.password(), null);
        UserData userOnRecord = dataAccessUser.get(userRequest);
        dataAccessUser.checkValidUser(userOnRecord);
        dataAccessUser.checkPasswordsEqual(userRequest, userOnRecord);
        AuthData auth = dataAccessAuth.create(userOnRecord);
        return new LoginResult(userOnRecord.username(), auth.authToken());
    }
    public void logout(LogoutRequest request) throws DataAccessException {
        AuthData auth = new AuthData(request.authToken(), null);
        dataAccessAuth.checkAuthTokenValid(auth);
        dataAccessAuth.delete(auth);
    }
    public void clearAllUsers(){ //might need to include the clear games and clear auth
        dataAccessAuth.clearAll();
        dataAccessUser.clearAll();
    }
}

