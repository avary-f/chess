package client;

import com.sun.nio.sctp.NotificationHandler;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ResponseException;

public class ClientPrelogin extends ChessClient{

    public ClientPrelogin(String serverUrl) {
        super(serverUrl);
        this.setState(State.LOGGEDOUT);
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
        if (params.length == 2) {
            setClientName(params[0]);
            LoginResult result = server.login(new LoginRequest(getClientName(), params[1]));
            setAuth(result.authToken());
            setState(State.LOGGEDIN);
            return String.format("You're logged in as %s.", getClientName());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            setClientName(params[0]);
            RegisterRequest reg = new RegisterRequest(getClientName(), params[1], params[2]);
            RegisterResult result = server.register(reg);
            setAuth(result.authToken());
            setState(State.LOGGEDIN);
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
