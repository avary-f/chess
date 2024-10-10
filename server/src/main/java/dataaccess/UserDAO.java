package dataaccess;

public interface UserDAO {
    public UserData get(UserData user);
    public UserData create(UserData user);
    public void delete(UserData user);

}
