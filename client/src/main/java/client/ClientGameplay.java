package client;

import model.GameData;
import server.ResponseException;
import server.websocket.ServerMessageHandler;
import service.GameService;

public class ClientGameplay extends ChessClient{

    public ClientGameplay(String serverUrl, ServerMessageHandler serverMessageHandler, String auth, String clientName, GameData game, String teamColor) {
        super(serverUrl, serverMessageHandler);
        this.setState(State.GAMEPLAY);
        this.setAuth(auth);
        setTeamColor(teamColor);
        this.setClientName(clientName);
        this.setGame(game);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "redraw" -> redraw();
            case "highlight" -> highlight(params);
//            case "make" -> makeMove(params);
//            case "resign" -> resign();
            case "leave" -> leave();
            default -> help();
        };
    }

    private String redraw() {
        BoardReader boardReaderMyColor = new BoardReader(this.getGame(), this.getTeamColor());
        boardReaderMyColor.drawChessBoard();
        return "";
    }

    private String highlight(String[] params) {
        if(params.length == 1) {
            BoardReader boardReaderMyColor = new BoardReader(this.getGame(), this.getTeamColor());
            boardReaderMyColor.drawChessBoard();
            return "";
        } else{
            throw new ResponseException(400, "Expected: <RowCol> (ex: e5) ");
        }
    }

    private String leave() {
        //by default make it like the other game (in case person leaving is an observer)
        ws.leave(getAuth(), getGame());
        setState(State.LOGGEDIN); //move out of gameplay
        setGame(null);
        setTeamColor(null);
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
