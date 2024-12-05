package client;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ResponseException;

import static ui.EscapeSequences.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BoardReader { //prints out the board
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final GameData game;
    private final ChessGame.TeamColor playerColor;
    private final static int BOARD_SIZE_IN_SQUARES = 8;
    private final ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h")); //white orientation by default
    private final ArrayList<String> rows = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8")); //white orientation by default
    private final ArrayList<String> columnsReversed = columns;
    private final ArrayList<String> rowsReversed = rows;

    public BoardReader(GameData game, String playerColor) {
        if(playerColor.equals("WHITE")){
            this.playerColor = ChessGame.TeamColor.WHITE;
        } else{
            this.playerColor = ChessGame.TeamColor.BLACK;
            reverseOrinetation();
        }
        this.game = game;
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public ArrayList<String> getRows() {
        return rows;
    }

    private void reverseOrinetation(){
        Collections.reverse(columnsReversed);
        Collections.reverse(rowsReversed);
    }

    private ChessPosition reversePosition(ChessPosition position){
        int tempR = position.getRow();
        int tempC = position.getColumn();
        if(playerColor.equals(ChessGame.TeamColor.BLACK)){ //change orientation if necessary
            //int used in the reversal process
            int reverseCount = 9;
            tempR = reverseCount - tempR;
            tempC = reverseCount - tempC;
        }
        return new ChessPosition(tempR, tempC);
    }

    private void drawHeaders() {
        setBlack(out);
        out.print(" ");
        out.print(EMPTY.repeat(2)); //empty spaces for alignment
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            printCharText(columnsReversed.get(col)); //CHANGED HERE
            if (col < BOARD_SIZE_IN_SQUARES - 1) { //help line up the columns
                out.print("   ");
            }
        }
        out.println();
    }

    private void printCharText(String c) {
        out.print(SET_BG_COLOR_BLACK); //sets background color to black
        out.print(SET_TEXT_COLOR_WHITE); //sets front color to white
        out.print(c);
        setBlack(out); //set the background and foreground to be black
    }

    private void setup(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public void drawChessBoard() {
        highlightChessBoard(Collections.emptyList(), null);
    }

    public void drawHighlightChessBoard(String input) {
        ChessPosition position = getPiecePosition(input);
        Collection<ChessMove> validMoves = game.game.validMoves(position);
        if (game.game.getBoard().getPiece(position) == null) {
            throw new ResponseException(400, "Invalid chess position");
        }
        highlightChessBoard(validMoves, position);
    }

    public void highlightChessBoard(Collection<ChessMove> validMoves, ChessPosition position) {
        boolean highlight = position != null && validMoves != null;
        if(position != null){
            position = reversePosition(position);
        }
        setup();
        drawHeaders();
        for (int row = BOARD_SIZE_IN_SQUARES; row > 0 ; row--) {
            out.print(EMPTY);
            printCharText(rowsReversed.get(row - 1));
            out.print(EMPTY);
            for (int col = 1; col <= BOARD_SIZE_IN_SQUARES; col++) {
                if ((row + col) % 2 == 0) {
                    setGrey(out);
                    if(highlight) {checkForHighlight(false, validMoves, row, col, position);} //0 = dark squares
                } else {
                    setMagenta(out);
                    if(highlight) {checkForHighlight(true, validMoves, row, col, position);} //1 = light squares
                }
                ChessPosition curPos = reversePosition(new ChessPosition(row, col));
                ChessPiece piece = game.game.getBoard().getPiece(curPos);
                if (piece != null) {
                    printPlayer(out, curPos, piece.getTeamColor()); // print the piece on this square

                } else {
                    out.print(WHITE_PAWN); // empty space
                }
                setBlack(out); //reset for the next square
            }
            out.print(EMPTY);
            printCharText(rows.get(row - 1));
            out.println(); //move to next row
        }
        drawHeaders();
        if(highlight && validMoves.isEmpty()){
            out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_RED + "This piece cannot make any moves");
        }
    }

    private void checkForHighlight(boolean lightSquare, Collection<ChessMove> validMoves, int row, int col, ChessPosition position) {
        if(position.equals(new ChessPosition(row, col))){
            setColor(lightSquare);
        }
        for (ChessMove move : validMoves) {
            if(playerColor.equals(ChessGame.TeamColor.BLACK)){
                move = new ChessMove(move.getStartPosition(), new ChessPosition(9 - move.getEndPosition().getRow(),
                        9 - move.getEndPosition().getColumn()), move.promotionPiece());
            }//reverse if needed
            if (move.getEndPosition().equals(new ChessPosition(row, col))) {
                setColor(lightSquare);
                break;
            }
        }
    }

    private ChessPosition getPiecePosition(String position) {
        return new ChessPosition(getIndex(rows, position.substring(1, 2)), getIndex(columns, position.substring(0, 1)));
    }

    private int getIndex(ArrayList<String> list, String c) {
        if (!list.contains(c)) {
            return -1; // Invalid column
        }
        int index = list.indexOf(c);
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            return index + 1;
        } else {
            return 8 - index;
        }
    }

    private void printPlayer(PrintStream out, ChessPosition pos, ChessGame.TeamColor teamColor) {
        if(teamColor.equals(ChessGame.TeamColor.WHITE)){
            out.print(SET_TEXT_COLOR_WHITE);
        }
        else{
            out.print(SET_TEXT_COLOR_BLACK);
        }
        String mappedPeice = mappedPiece(game.game.getBoard().getPiece(pos));
        out.print(mappedPeice);
    }

    private String mappedPiece(ChessPiece piece){
        return switch (piece.getPieceType()) {
            case QUEEN -> BLACK_QUEEN;
            case PAWN -> BLACK_PAWN;
            case KING -> BLACK_KING;
            case KNIGHT -> BLACK_KNIGHT;
            case ROOK -> BLACK_ROOK;
            case BISHOP -> BLACK_BISHOP;
        };
    }

    private void setColor(boolean lightSquare){
        if (lightSquare) {
            setLightBlue(out);
        } else {
            setDarkBlue(out);
        }
    }

    private void setGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private void setMagenta(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }


    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setLightBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private void setDarkBlue(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_BLUE);
        out.print(SET_TEXT_COLOR_DARK_BLUE);
    }
}
