package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
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

    public AuthService getServiceAuth(){
        return serviceAuth;
    }
    public RegisterResult register(RegisterRequest request) throws Exception {
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData user = new UserData(request.username(), hashedPassword, request.email());
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
        if(dataAccessUser.get(user) != null && dataAccessUser.get(user).username() != null){
            throw new AlreadyTakenException();
        }
    }
    private void checkValidUser(UserData user) throws UnauthorizedException{
        if(user == null){
            throw new UnauthorizedException();
        }
    }
    private void checkPasswordsEqual(UserData userToHash, UserData userToCompare) throws UnauthorizedException{
        var userHashed = dataAccessUser.get(new UserData(userToCompare.username(), null, null));
        if(!BCrypt.checkpw(userToHash.password(), userHashed.password())){
            throw new UnauthorizedException();
        }
//        BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
}

