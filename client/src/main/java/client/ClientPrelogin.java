package client;

import com.sun.nio.sctp.NotificationHandler;
import request.LoginRequest;
import request.RegisterRequest;
import server.ResponseException;

public class ClientPrelogin extends ChessClient{

    public ClientPrelogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd) {
        return switch (cmd) {
            case "login" -> login();
            case "register" -> register();
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
        if (params.length >= 3) {
            setState(State.LOGGEDIN);
            setClientName(params[0]);
            server.register(new RegisterRequest(getClientName(), params[1], params[2]));
            return String.format("You're logged in as %s.", getClientName());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
       return """
            register <username> <password> <email>
            login <username> <password>
            quit
            help
            """;
    }

}
