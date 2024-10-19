package server;

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException() {
        super("already taken");
    }
}
