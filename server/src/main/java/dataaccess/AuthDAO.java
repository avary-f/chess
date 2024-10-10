package dataaccess;

public interface AuthDAO {
    public void delete(AuthData data);
    public AuthData get(AuthData data);
    public AuthData create(); //does this need to be associated with a user?
}
