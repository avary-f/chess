package dataaccess;
import model.UserData;
import static dataaccess.MysqlDAO.execute;

public class MysqlUserDAO extends MysqlDAO implements UserDAO {

    @Override
    public UserData get(UserData user) {
        String usernameStatement = "SELECT username FROM users WHERE username = ?";
        String passwordStatement = "SELECT password FROM users WHERE username = ?";
        String emailStatement = "SELECT email FROM users WHERE username = ?";
        String usernameResult = (String) execute(usernameStatement, user.username());
        String passwordResult = (String) execute(passwordStatement, user.username());
        String emailResult = (String) execute(emailStatement, user.username());
        if(usernameResult != null){ //does not exist in DB
            return new UserData(usernameResult, passwordResult, emailResult);
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
