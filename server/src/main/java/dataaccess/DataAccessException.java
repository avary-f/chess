package dataaccess;

public class DataAccessException extends RuntimeException{
    public DataAccessException(String error) {super("Error: " + error);}
}
