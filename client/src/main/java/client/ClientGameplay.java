package client;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        printPrompt();
        System.out.print("Are you sure you want to resign? (y/n)");
        System.out.println();
        printPrompt();
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        try {
            if(evalResign(line)){
                ws.resign(getAuth(), getGame());
            }
            else{
                printPrompt();
                System.out.println("Resign request cancelled. Continue playing!");
            }
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
        }
        return "";
    }

    public boolean evalResign(String input) {
        var tokens = input.toLowerCase().split(" ");
        if(tokens.length == 1){
            String response = tokens[0];
            return response.equals("y") || response.equals("yes");
        }
        return false;
    }

    private void checkValidInput(String[] input){
        for(var s: input){
            if((s.length() != 2) || !getBoardReader().getColumns().contains(s.substring(0, 1))
                    || !getBoardReader().getRows().contains(s.substring(1, 2))){ //has to be valid input
                throw new ResponseException(400, "Invalid chess position");
            }
        }
    }

    private String makeMove(String[] params) throws ResponseException {
        if(params.length == 2){
            checkValidInput(params);
        }
        else if(params.length == 3){
            String[] newParams = {params[0], params[1]};
            checkValidInput(newParams);
            checkValidInputPromotion(params[2]);
        }
        if(params.length == 2 || params.length == 3) { //the 3rd param is the promotion piece
            if(getGame().game.isGameEnded()){
                throw new ResponseException(422, "Error: Game has ended");
            }
            ChessPiece.PieceType promotion = null;
            if (params.length == 3 && !params[2].trim().isEmpty()) { // prevent empty or whitespace promotion piece
                promotion = getPromotionPieceType(params[2].trim());
            }
            String moveFrom = params[0];
            String moveTo = params[1];
            ChessMove move = new ChessMove(convertColumns(moveFrom), convertColumns(moveTo), promotion);
            ws.makeMove(getAuth(), getGame(), move);
            return "";
        } else{
            throw new ResponseException(400, "Expected: <Current_ColRow> <Desired_ColRow> [PromotionPiece] (ex: e2 e3 queen)");
        }
    }

    private void checkValidInputPromotion(String s){
        if(!(s.equals("queen") || s.equals("rook") || s.equals("bishop") || s.equals("knight"))){ //has to be valid input
            throw new ResponseException(400, "Expected promotion piece: queen | rook | bishop | knight ");
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
        ws.redraw(getAuth(), getGame());
        return "";
    }

    private String highlight(String[] params) {
        if(params.length == 1 && params[0].length() == 2) {
            checkValidInput(params);
            String highlightSquare = params[0];
            ws.highlight(getAuth(), getGame(), highlightSquare);
            return "";
        } else{
            throw new ResponseException(400, "Expected: <ColRow> (ex: e5) ");
        }
    }

    private String leave() {
        //by default make it like the other game (in case person leaving is an observer)
        ws.leave(getAuth(), getGame());
        moveOutOfGamePlay();
        return "You have left the game. Type 'help' to continue.";
    }

    private void moveOutOfGamePlay(){
        setState(State.LOGGEDIN); //move out of gameplay
        setGame(null);
        setTeamColor(null);
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
