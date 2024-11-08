package client;

import com.sun.nio.sctp.NotificationHandler;
import server.ResponseException;

public class ClientPrelogin extends ChessClient implements ChessClientInterface{

    public ClientPrelogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd) {
        return switch (cmd) {
            case "login" -> {
                login();
                ClientPostlogin clientPostlogin = new ClientPostlogin(serverurl);
                clientPostlogin.eval();
            }
            case "register" -> register();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            setState(State.LOGGEDIN);
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <yourname>");
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
