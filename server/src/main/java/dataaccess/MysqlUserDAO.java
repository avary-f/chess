package dataaccess;
import model.UserData;
import static dataaccess.MysqlDAO.execute;

public class MysqlUserDAO extends MysqlDAO implements UserDAO {

    @Override
    public UserData get(UserData user) {
        String usernameStatement = "SELECT username FROM users WHERE username = ?";
        String passwordStatement = "SELECT password FROM users WHERE username = ?";
        String emailStatement = "SELECT username FROM users WHERE username = ?";
        String userResult = (String) execute(usernameStatement, user.username());
        if(userResult != null){ //does not exist in DB
            return new UserData(usernameStatement, passwordStatement, emailStatement);
        }
        return null;
    }

    @Override
    public void create(UserData user) {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        execute(statement, user.username(), user.password(), user.email());
    }

    @Override
    public void clearAll() {
        String statement = "TRUNCATE users";
        execute(statement);
    }
}
