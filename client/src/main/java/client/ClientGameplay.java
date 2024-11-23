package client;

import model.GameData;
import server.ResponseException;
import server.websocket.ServerMessageHandler;

public class ClientGameplay extends ChessClient{
    private String teamColor;


    public ClientGameplay(String serverUrl, ServerMessageHandler serverMessageHandler, String auth, String clientName, GameData game, String teamColor) {
        super(serverUrl, serverMessageHandler);
        this.setState(State.GAMEPLAY);
        this.setAuth(auth);
        this.teamColor = teamColor;
        this.setClientName(clientName);
        this.setGame(game);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "redraw" -> redraw();
//            case "highlight" -> highlight(params);
//            case "make" -> makeMove(params);
//            case "resign" -> resign();
//            case "leave" -> leave();
            default -> help();
        };
    }

    private String redraw() {
        BoardReader boardReaderMyColor = new BoardReader(getGame(), getTeamColor());
        boardReaderMyColor.drawChessBoard();
        return "";
    }

    private String leave() {
        setState(State.LOGGEDIN); //move out of gameplay
        setGame(null);
        setTeamColor(null);
        //create a new game data and stick it back in the dB
        //use existing update game functionality
        //through websocket, leave is one of the commands that it sends
        return "You have left the game. Type 'help' to continue.";
    }

//    private String highlight(String[] params){
//        if(params.length == 1){
//
//        }
//        else{
//            throw new ResponseException(400, "")
//        }
//    }

    @Override
    public String help() {
        return """
            redraw chess board
            highlight legal moves <RowCol>
            make move <Current_RowCol> <Desired_RowCol>
            resign
            leave
            help
            """;
    }

}
