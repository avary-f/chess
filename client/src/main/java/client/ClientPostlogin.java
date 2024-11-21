package client;
import model.GameData;
import request.*;
import result.ListResult;
import server.ResponseException;

import java.util.HashMap;

import static ui.EscapeSequences.RED;

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
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "delete" -> delete(params);
            default -> help();
        };
    }

    private int validateIDInput(String gameID){
        try {
            return Integer.parseInt(gameID);
        }
        catch(NumberFormatException ex){ //this is an exception thrown by parseInt from the java standard lib
            throw new ResponseException(400, "Invalid input format. Expected: <ID> [WHITE|BLACK]");
        }
    }

    private GameData getGameFromGameIndexMap(int index){
        HashMap<Integer, GameData> gameIndexMap = getGameIndex();
        if(gameIndexMap.isEmpty()){ //if no games exist
            throw new ResponseException(400, "No games exist yet. Type 'create <GAME_NAME>' to create a new game.");
        }
        GameData game = gameIndexMap.get(index);
        if(game == null){
            throw new ResponseException(400, "Game ID = " + index + " not found.");
        }
        return game;
    }

    public String delete(String... params) throws ResponseException {
        if (params.length == 1) {
            String passwordForDeleting = params[0];
            if(passwordForDeleting.equals("eraseallofit")) {
                server.deleteAll();
            }
            else{
                throw new ResponseException(400, RED + "Invalid password.");
            }
            setState(State.LOGGEDOUT);
            return RED + "Successfully deleted all data in auth, user, and game tables.";
        }
        else{
            throw new ResponseException(400, RED + "Enter password to delete all data.");
        }
    }

    public String observe(String... params) {
        if(params.length == 1){
            int index = validateIDInput(params[0]);
            GameData game = getGameFromGameIndexMap(index);
            BoardReader boardToObserve = new BoardReader(game, "WHITE"); //default to watching white as observer
            boardToObserve.drawChessBoard();
            setGame(game);
            setTeamColor("WHITE");
            setState(State.GAMEPLAY);
            return "You joined " + game.gameName() + " as an observer.\n" ;
        }
        else{
            throw new ResponseException(400, "Expected: <ID> ");
        }
    }

    public String join(String... params) {
        if(params.length == 2){
            int index = validateIDInput(params[0]);
            String playerColor = params[1].toUpperCase(); //convert their entry to uppercase
            if(playerColor.equals("WHITE") || playerColor.equals("BLACK")){
                GameData game = getGameFromGameIndexMap(index);
                try{
                    server.joinGame(new JoinRequest(getAuth(), playerColor, game.gameID()));
                    BoardReader boardReaderMyColor = new BoardReader(game, playerColor); //orient board based on your color
                    boardReaderMyColor.drawChessBoard();
                    setGame(game);
                    setTeamColor(playerColor);
                    setState(State.GAMEPLAY);
                    return "You joined " + game.gameName() + " as " + playerColor +"\n";
                } catch (RuntimeException ex) {
                    throw new ResponseException(403, "Player already taken.");
                }
            }
            else{
                throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
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
            gameIndex++;
        }
        return gameIndexNameMap;
    }
}
