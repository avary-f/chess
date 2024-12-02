package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ResponseException;

import java.util.ArrayList;
import java.util.List;

public class ClientGameplay extends ChessClient{

    public ClientGameplay(String serverUrl, String auth, String clientName, GameData game, String teamColor) {
        super(serverUrl);
        this.setState(State.GAMEPLAY);
        this.setAuth(auth);
        setTeamColor(teamColor);
        this.setClientName(clientName);
        this.setGame(game);
        updateBoardReader(new BoardReader(getGame(), getTeamColor()));
    }

    @Override
    public String performCmd(String cmd, String[] params) throws RuntimeException {
        return switch (cmd) {
            case "redraw" -> redraw();
            case "highlight" -> highlight(params);
            case "move" -> makeMove(params);
            case "resign" -> resign();
            case "leave" -> leave();
            default -> help();
        };
    }

    private String resign() {
        getGame().game.setEndOfGame();
        ws.resign(getAuth(), getGame());
        return "Game over. You have resigned";
    }

    private void checkValidInput(String[] input){
        for(String s: input){
            if(s.length() != 2 || !getBoardReader().getColumns().contains(s.substring(0, 1))
                    || !getBoardReader().getRows().contains(s.substring(1, 2))){ //has to be valid input
                throw new ResponseException(400, "Invalid chess position");
            }
        }
    }

    private String makeMove(String[] params) throws ResponseException {
        if(params.length == 2 || params.length == 3) { //the 3rd param is the promotion piece
            checkValidInput(params);
            if(getGame().game.isGameEnded()){
                throw new ResponseException(422, "Error: Game has ended");
            }
            ChessPiece.PieceType promotion = null;
            if(params.length == 3){
                promotion = getPromotionPieceType(params[2]);
            }
            String moveFrom = params[0];
            String moveTo = params[1];
            ChessMove move = new ChessMove(convertColumns(moveFrom), convertColumns(moveTo), promotion);
            if(!getGame().game.getTeamTurn().equals(convertColors(getTeamColor()))){
                throw new ResponseException(422, "Error: Waiting on the other player's move...");
            }
            ws.makeMove(getAuth(), getGame(), move);
            return "";
        } else{
            throw new ResponseException(400, "Expected: <Current_ColRow> <Desired_ColRow> PromotionPiece (ex: e2 e3 queen) ");
        }
    }

    private ChessGame.TeamColor convertColors(String color){
        if(color.equals("WHITE")){
            return ChessGame.TeamColor.WHITE;
        }
        else{
            return ChessGame.TeamColor.BLACK;
        }
    }

    private ChessPiece.PieceType getPromotionPieceType(String type) {
        return switch (type.toLowerCase()){
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new ResponseException(400, "Expected promotion piece: queen | rook | bishop | knight ");
        };
    }

    private ChessPosition convertColumns(String colRow){
        ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));
        return new ChessPosition(Integer.parseInt(colRow.substring(1,2)), columns.indexOf(colRow.substring(0,1)) + 1);
    }

    private String redraw() {
        getBoardReader().drawChessBoard();
        return "";
    }

    private String highlight(String[] params) {
        if(params.length == 1 && params[0].length() == 2) {
            checkValidInput(params);
            String input = params[0];
            getBoardReader().drawHighlightChessBoard(input);
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
