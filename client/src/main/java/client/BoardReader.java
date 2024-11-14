package client;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import static ui.EscapeSequences.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BoardReader { //prints out the board
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private GameData game;
    private final String playerColor;
    private final int BOARD_SIZE_IN_SQUARES = 8;
    private final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private final ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h")); //white orientation by default
    private final ArrayList<String> rows = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8")); //white orientation by default
    private int reverseCount = 9; //int used in the reversal process

    public BoardReader(GameData game, String playerColor) {
        this.playerColor = playerColor;
        this.game = game;
        if(playerColor.equals("BLACK")){
            reverseOrinetation();
        }
    }

    private void reverseOrinetation(){
        Collections.reverse(columns); //modifies columns directly
        Collections.reverse(rows); //modifies rows directly
    }

    private void drawHeaders() {
        setBlack(out);
        out.print(" ");
        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS * 2)); //empty spaces for alignment
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            printCharText(columns.get(col));
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
        setup();
        drawHeaders();
        for (int row = 1; row <= BOARD_SIZE_IN_SQUARES; row++) {
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            printCharText(rows.get(row - 1));
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            for (int col = 1; col <= BOARD_SIZE_IN_SQUARES; col++) {
                if ((row + col) % 2 == 0) {
                    setWhite(out);
                } else {
                    setMagenta(out);
                }
                int tempR = row;
                int tempC = col;
                if(playerColor.equals("BLACK")){ //change orientation if necessary
                    tempR = reverseCount - tempR;
                    tempC = reverseCount - tempC;
                }
                ChessPiece piece = game.game.getBoard().getPiece(new ChessPosition(tempR, tempC));
                if (piece != null) {
                    printPlayer(out, tempR, tempC, piece.getTeamColor()); // Print the piece on this square

                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS)); // Empty space in non-middle rows of square
                }
                setBlack(out); //reset for the next square
            }
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            printCharText(rows.get(row - 1));
            out.println(); //move to next row
        }
        drawHeaders();
    }

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void setMagenta(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void printPlayer(PrintStream out, int row, int col, ChessGame.TeamColor teamColor) {
        if(teamColor.equals(ChessGame.TeamColor.WHITE)){
            out.print(SET_TEXT_COLOR_WHITE);
        }
        else{
            out.print(SET_TEXT_COLOR_BLACK);
        }
        String mappedPeice = mappedPiece(game.game.getBoard().getPiece(new ChessPosition(row, col)));
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
}
