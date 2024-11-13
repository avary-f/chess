package client;
import model.GameData;
import request.*;
import result.JoinResult;
import result.ListResult;
import server.ResponseException;

import java.util.HashMap;

public class ClientPostlogin extends ChessClient{
    public ClientPostlogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "create" -> create(params); //does not join the user to the game, only makes it in the server
            case "list" -> list(params);
            case "join" -> join(params);
//            case "observe" -> observe(params);
            case "logout" -> logout();
            default -> help();
        };
    }

    public String join(String... params){
        if(params.length == 2){
            try{
                int index = Integer.parseInt(params[0]);
                String playerColor = params[1].toUpperCase();

                if(playerColor.equals("WHITE") || playerColor.equals("BLACK")){
                    HashMap<Integer, GameData> gameIndexMap = getGameIndex();
                    if(gameIndexMap.isEmpty()){ //if no games exist
                        return "No games exist yet. Type 'create <GAME_NAME>' to create a new game.";
                    }
                    GameData game = gameIndexMap.get(index);
                    if(game == null){
                        throw new ResponseException(400, "Game ID = " + index + " not found.");
                    }
                    server.joinGame(new JoinRequest(getAuth(), playerColor, game.gameID()));
                    return "You joined " + gameIndexMap.get(index).gameName() + " as " + playerColor;
                }
                else{
                    throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
                }
            }
            catch(NumberFormatException ex){ //this is an exception thrown by parseInt from the java standard lib
                throw new ResponseException(400, "Invalid input format. Expected: <ID> [WHITE|BLACK]");
            }
        }
        else{
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }
    }

    public String logout(String... params) {
        if(params.length == 0) {
            server.logout(new LogoutRequest(getAuth()));
            setState(State.LOGGEDOUT);
            return "Logged out.";
        }
        else{
            throw new ResponseException(400, "Expected: 'logout'");
        }
    }

    public String list(String... params) {
        if(params.length == 0) {
            ListResult resultList = server.listGames(new ListRequest(getAuth()));
            StringBuilder result = new StringBuilder();
            if (resultList.games().isEmpty()) {
                return "No games created. Type 'create <GAME_NAME>' to create a new game.";
            }
            int gameNumber = 1;
            for (GameData game : resultList.games()) {
                String white = (game.whiteUsername() == null) ? "no player" : game.whiteUsername();
                String black = (game.blackUsername() == null) ? "no player" : game.blackUsername();
                result.append(gameNumber).append(". ").append(game.gameName()).append(", white = ").append(white)
                        .append(", black = ").append(black).append("\n");
                gameNumber++;
            }
            return result.toString();
        }
        else{
            throw new ResponseException(400, "Expected: 'list'");
        }
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

    private HashMap<Integer, GameData> getGameIndex(){
        HashMap<Integer, GameData> gameIndexNameMap = new HashMap<>();
        ListResult resultList = server.listGames(new ListRequest(getAuth()));
        if (resultList.games().isEmpty()) {
            return gameIndexNameMap;
        }
        int gameIndex = 1;
        for(GameData game: resultList.games()){
            gameIndexNameMap.put(gameIndex, game);
        }
        return gameIndexNameMap;
    }
}
