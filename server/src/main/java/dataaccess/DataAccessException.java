package dataaccess;

public class DataAccessException extends Exception{
    public DataAccessException(String error) {super("Error: " + error);}
}
