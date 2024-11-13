package client;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.GameData;
import request.*;
import result.ListResult;
import server.ResponseException;

public class ClientPostlogin extends ChessClient{
    public ClientPostlogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "create" -> create(params); //does not join the user to the game, only makes it in the server
            case "list" -> list();
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

    public String list() {
        System.out.println("testing");
        ListResult resultList = server.listGames(new ListRequest(getAuth()));
        StringBuilder result = new StringBuilder();
        var gson = new Gson();
        if(resultList.games().isEmpty()){
            return "No games created. Type 'create' to make a new game.";
        }
        for(GameData game: resultList.games()){
           result.append(gson.toJson(game)).append('\n');
        }
        return "list of games";
        //return result.toString();
    }

    public String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            server.createGame(new CreateRequest(gameName), getAuth());
            return String.format("%s created. Type 'list' to see your game.", gameName);
        }
        throw new ResponseException(400, "Expected: <GAME_NAME>");
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
