package dataaccess;

import model.UserData;

public class MysqlUserDAO implements UserDAO{
    DatabaseManager databaseManager;

    public MysqlUserDAO(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

    @Override
    public UserData get(UserData user) {
        return null;
    }

    @Override
    public void create(UserData user) {

    }

    @Override
    public void clearAll() {

    }
}
