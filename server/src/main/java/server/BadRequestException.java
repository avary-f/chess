package server;

public class BadRequestException extends Exception{
    public BadRequestException() {
        super("Bad request");
    }
}
