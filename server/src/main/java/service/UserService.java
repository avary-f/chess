package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;
import result.*;
import request.*;
import server.AlreadyTakenException;
import server.UnauthorizedException;

public class UserService {
    private final UserDAO dataAccessUser;
    private final AuthDAO dataAccessAuth;
    private final AuthService serviceAuth;

    public UserService(UserDAO dataAccessUser, AuthDAO dataAccessAuth){
        this.dataAccessAuth = dataAccessAuth;
        this.dataAccessUser = dataAccessUser;
        serviceAuth = new AuthService(dataAccessAuth);
    }
    public RegisterResult register(RegisterRequest request) throws Exception {
        UserData user = new UserData(request.username(), request.password(), request.email());
        checkUserNotTaken(user);
        dataAccessUser.create(user);
        AuthData auth = dataAccessAuth.create(user);
        return new RegisterResult(request.username(), auth.authToken());
    }
    public LoginResult login(LoginRequest request) throws Exception{
        UserData userRequest = new UserData(request.username(), request.password(), null);
        UserData userOnRecord = dataAccessUser.get(userRequest);
        checkValidUser(userOnRecord);
        checkPasswordsEqual(userRequest, userOnRecord);
        AuthData auth = dataAccessAuth.create(userOnRecord);
        return new LoginResult(userOnRecord.username(), auth.authToken());
    }
    public void logout(LogoutRequest request) throws Exception {
        AuthData auth = new AuthData(request.authToken(), null);
        serviceAuth.checkAuthTokenValid(auth);
        dataAccessAuth.delete(auth);
    }
    public void clearAllUserAuthData(){ //might need to include the clear games and clear auth
        dataAccessAuth.clearAll();
        dataAccessUser.clearAll();
    }
    private void checkUserNotTaken(UserData user) throws AlreadyTakenException {
        if(dataAccessUser.get(user).username() != null){
            throw new AlreadyTakenException();
        }
    }
    private void checkValidUser(UserData user) throws UnauthorizedException{
        if(user == null){
            throw new UnauthorizedException();
        }
    }
    private void checkPasswordsEqual(UserData user1, UserData user2) throws UnauthorizedException{
        if(!(user1.password().equals(user2.password()))){
            throw new UnauthorizedException();
        }
    }
}

