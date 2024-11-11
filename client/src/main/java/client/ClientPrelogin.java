package client;

import com.sun.nio.sctp.NotificationHandler;
import request.LoginRequest;
import request.RegisterRequest;
import server.ResponseException;

import java.util.Scanner;

import static ui.EscapeSequences.GREEN;
import static ui.EscapeSequences.RESET;

public class ClientPrelogin extends ChessClient{

    public ClientPrelogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            setState(State.LOGGEDIN);
            setClientName(params[0]);
            server.login(new LoginRequest(getClientName(), params[1]));
            return String.format("You're logged in as %s.", getClientName());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
//        printLoginPrompt();
//        Scanner scanner = new Scanner(System.in);
//        String line = scanner.nextLine();
//        System.out.println();
        if (params.length >= 3) {
            System.out.println("made it here");
            setClientName(params[0]);
            System.out.println("here");
            RegisterRequest reg = new RegisterRequest(getClientName(), params[1], params[2]);
            server.register(reg);
            System.out.println("here?");
            setState(State.LOGGEDIN);
            return String.format("You're logged in as %s.", getClientName());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
       return """
            register
            login
            quit
            help
            """;
    }

    public void printLoginPrompt(){
        System.out.println("\n" + RESET + "Enter credentials: <username> <password> <email> ");
        System.out.println();
        System.out.print("[LOGGED_OUT] >>> " + GREEN);
    }

}
