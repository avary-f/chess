package client;

import request.CreateRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import server.ResponseException;

public class ClientPostlogin extends ChessClient{
    public ClientPostlogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "create" -> create(params); //does not join the user to the game, only makes it in the server
//            case "list" -> list(params);
//            case "join" -> join(params);
//            case "observe" -> observe(params);
            case "logout" -> logout();
            default -> help();
        };
    }

    public String logout() {
        server.logout(new LogoutRequest(getAuth()));
        setState(State.LOGGEDOUT);
        return "Logged out.";
    }

    public String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            server.createGame(new CreateRequest(gameName));
            return String.format("%s game created. Type 'list' to see your game & 'join' to start playing.", gameName);
        }
        throw new ResponseException(400, "Expected: <GAME_NAME>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            setClientName(params[0]);
            RegisterRequest reg = new RegisterRequest(getClientName(), params[1], params[2]);
            server.register(reg);
            setState(State.LOGGEDIN);
            return String.format("You're logged in as %s.", getClientName());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    @Override
    public String help() {
        return """
            create <GAME_NAME> - a game
            list - games
            join <ID> [WHITE|BLACK] - a game
            observe <ID> - a game
            logout
            help
            """;
    }
}
