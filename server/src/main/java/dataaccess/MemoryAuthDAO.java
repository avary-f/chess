package dataaccess;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    private ArrayList<AuthData> list = new ArrayList<AuthData>(); //memory of authData objects
    //How are the auth data tokens connected to the users?


    @Override
    public void delete(AuthData data) {
    }

    @Override
    public AuthData get(AuthData data) {
        return null;
    }

    @Override
    public AuthData create() {
        return null;
    }
}
