package dataaccess;

import model.AuthData;
import model.UserData;

public class MysqlAuthDAO implements AuthDAO{
    @Override
    public void delete(AuthData data) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        MysqlDAO.executeUpdate(statement, data.authToken());
    }

    @Override
    public void clearAll() throws DataAccessException {
        var statement = "TRUNCATE auth";
        MysqlDAO.executeUpdate(statement);
    }

    @Override
    public AuthData get(AuthData data) {
        return null;
    }

    @Override
    public AuthData create(UserData data) {
        return null;
    }
}
