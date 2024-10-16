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
        if(dataAccessUser.get(user) != null){
            throw new DataAccessException("already taken");
        }
        dataAccessUser.create(user);
        AuthData auth = dataAccessAuth.create(user);
        return new RegisterResult(request.username(), auth.authToken());
    }
    public LoginResult login(LoginRequest request) throws DataAccessException{
        UserData userRequest = new UserData(request.username(), request.password(), null);
        UserData userOnRecord = dataAccessUser.get(userRequest);
        if(!isValidUser(userOnRecord)){
            throw new DataAccessException("unauthorized");
        }
        else if(!isPasswordsEqual(userRequest, userOnRecord)){
            throw new DataAccessException("unauthorized");
        }
        AuthData auth = dataAccessAuth.create(userOnRecord);
        return new LoginResult(userOnRecord.username(), auth.authToken());
    }
    public void logout(LogoutRequest request) throws DataAccessException {
        AuthData auth = new AuthData(request.authToken(), null);
        auth = dataAccessAuth.get(auth);
        if(!isAuthTokenValid(auth)){
            throw new DataAccessException("unauthorized");
        }
        dataAccessAuth.delete(auth);
    }
    public boolean isAuthTokenValid(AuthData auth){
        return auth != null;
    }
    private boolean isValidUser(UserData user){
        return user != null;
    }
    private boolean isPasswordsEqual(UserData user1, UserData user2){
        return user1.password().equals(user2.password());
    }
//    public void clearAllUsers(){}
}

