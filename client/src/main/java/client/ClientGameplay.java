package client;

import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import server.ResponseException;
import server.websocket.ServerMessageHandler;

import java.util.ArrayList;
import java.util.List;

public class ClientGameplay extends ChessClient{

    private final BoardReader boardReader;

    public ClientGameplay(String serverUrl, ServerMessageHandler serverMessageHandler, String auth, String clientName,
                          GameData game, String teamColor) {
        super(serverUrl, serverMessageHandler);
        this.setState(State.GAMEPLAY);
        this.setAuth(auth);
        setTeamColor(teamColor);
        this.setClientName(clientName);
        this.setGame(game);
        boardReader = new BoardReader(getGame(), getTeamColor());
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "redraw" -> redraw();
            case "highlight" -> highlight(params);
            case "move" -> makeMove(params);
//            case "resign" -> resign();
            case "leave" -> leave();
            default -> help();
        };
    }

    private void checkValidInput(String[] input){
        for(String s: input){
            if(s.length() != 2 || !boardReader.getColumns().contains(s.substring(0, 1))
                    || !boardReader.getRows().contains(s.substring(1, 2))){ //has to be valid input
                throw new ResponseException(400, "Invalid chess position");
            }
        }
    }

    private String makeMove(String[] params) {
        if(params.length == 2 || params.length == 3) { //the 3rd param is the promotion piece
            checkValidInput(params);
            String moveFrom = params[0];
            String moveTo = params[1];
            ChessMove move = new ChessMove(convertColumns(moveFrom), convertColumns(moveTo), null);
            //is passing in a null value for the promotion piece bad? Will that be changed later?
            ws.makeMove(getAuth(), getGame(), move);
            boardReader.drawMoveChessBoard(move);
            //print the board where the piece was moved to
            return "";
        } else{
            throw new ResponseException(400, "Expected: <Current_ColRow> <Desired_ColRow> (ex: e2 e3) ");
        }
    }

    private ChessPosition convertColumns(String colRow){
        ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));
        return new ChessPosition(Integer.parseInt(colRow.substring(1,2)), columns.indexOf(colRow.substring(0,1)));
    }

    private String redraw() {
        boardReader.drawChessBoard();
        return "";
    }

    private String highlight(String[] params) {
        if(params.length == 1 && params[0].length() == 2) {
            checkValidInput(params);
            String input = params[0];
            boardReader.drawHighlightChessBoard(input);
            return "";
        } else{
            throw new ResponseException(400, "Expected: <ColRow> (ex: e5) ");
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

    @Override
    public String help() {
        return """
            redraw chess board
            highlight legal moves <ColRow>
            move <Curr_ColRow> <Desired_ColRow>
            resign
            leave
            help
            """;
    }

}
