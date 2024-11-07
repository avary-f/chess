package client;

import com.sun.nio.sctp.NotificationHandler;
import server.ResponseException;

import java.util.Arrays;

public class ClientPrelogin extends ChessClient {
    public ClientPrelogin(String serverUrl, NotificationHandler notificationHandler) {
        super(serverUrl, notificationHandler);
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login();
                case "register" -> register();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
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
       String helpString = """
                    register <username> <password> <email>
                    login <username> <password>
                    quit
                    help
                    """;
        if (state == State.SIGNEDOUT) {
            return helpString;
        }
        else{
            ClientPostlogin clientPostlogin = new ClientPostlogin(serverurl, notificationHandler);
            clientPostlogin.help();
            clientPostlogin.eval();
            //not sure how to do the above part
            return helpString;
        }
    }

}
