package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class MysqlAuthDAO extends MysqlDAO implements AuthDAO{
    @Override
    public void delete(AuthData data) {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        //? => this is a placeholder that is provided at runtime
        executeUpdate(statement, data.authToken());
    }

    @Override
    public void clearAll() {
        String statement = "TRUNCATE auths"; //clears the whole table
        executeUpdate(statement);
    }

    @Override
    public AuthData get(AuthData data) {
        String statement = "SELECT username FROM auths WHERE authToken = ?";
        String usernameResult = executeQuery(statement, data.authToken());
        if(!Objects.equals(usernameResult, "")){ //does not exist in DB
            return new AuthData(data.authToken(), usernameResult);
        }
        return null;

    }

    @Override
    public AuthData create(UserData user) {
        String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        executeUpdate(statement, authToken, user.username());
        return new AuthData(authToken, user.username());
    }

    public boolean isEmpty(){
        String statement = "SELECT * FROM auths";
        String resultUser = executeQuery(statement);
        return resultUser.isEmpty();
    }
}
