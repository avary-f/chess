package client;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BoardReader { //prints out the board
    private static GameData game = new GameData(123, "whiteuser", "blackuser", "test", new ChessGame());
    private static String playerColor = "WHITE";
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final ArrayList<String> columns = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h")); //white orientation by default
    private static final ArrayList<String> rows = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8")); //white orientation by default

    public BoardReader(GameData game, String playerColor) {
        this.game = new GameData(123, "whiteuser", "blackuser", "test", new ChessGame());
        this.playerColor = "BLACK";
        if(playerColor.equals("BLACK")){
            reverseOrinetation();
        }
    }

    public static String getPlayerColor() {
        return playerColor;
    }

    public static GameData getGame() {
        return game;
    }

    private void reverseOrinetation(){
        Collections.reverse(columns); //modifies columns directly
        Collections.reverse(rows); //modifies rows directly
    }

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawHeaders(out);
//        drawFooters(out);
//        drawRowsLeft(out);
//        drawRowsRight(out);
        drawChessBoard(out);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

//    private static void

    private static void drawHeaders(PrintStream out) {
        setBlack(out);
        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS * 2)); //empty spaces for alignment
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            printCharText(out, columns.get(col));
            if (col < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS)); // add padded space after each char except last one
            }
        }
        out.println();
    }

    private static void printCharText(PrintStream out, String c) {
        out.print(SET_BG_COLOR_BLACK); //sets background color to black
        out.print(SET_TEXT_COLOR_WHITE); //sets front color to white
        out.print(c);
        setBlack(out); //set the background and foreground to be black
    }

    private static void drawChessBoard(PrintStream out) {
        for (int row = 1; row <= BOARD_SIZE_IN_SQUARES; row++) {
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            printCharText(out, rows.get(row - 1));
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            for (int col = 1; col <= BOARD_SIZE_IN_SQUARES; col++) {
                if ((row + col) % 2 == 0) {
                    setWhite(out);
                } else {
                    setMagenta(out);
                }
                ChessPiece piece = game.game.getBoard().getPiece(new ChessPosition(row, col));
                if (piece != null) {
                    printPlayer(out, row, col, piece.getTeamColor()); // Print the piece on this square
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS)); // Empty space in non-middle rows of square
                }

                setBlack(out); // Reset to black for next square
            }
            out.println(); // Move to the next row after finishing the columns
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setMagenta(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, int row, int col, ChessGame.TeamColor teamColor) {
        if(teamColor.equals(ChessGame.TeamColor.WHITE)){
            out.print(SET_TEXT_COLOR_BLACK);
        }
        else{
            out.print(SET_TEXT_COLOR_WHITE);
        }
        String mappedPeice = mappedPiece(game.game.getBoard().getPiece(new ChessPosition(row, col)));
        out.print(mappedPeice);
    }

    private static String mappedPiece(ChessPiece piece){
        return switch (piece.getPieceType()) {
            case QUEEN -> isWhite(piece) ? WHITE_QUEEN : BLACK_QUEEN;
            case PAWN -> isWhite(piece) ? WHITE_PAWN : BLACK_PAWN;
            case KING -> isWhite(piece) ? WHITE_KING : BLACK_KING;
            case KNIGHT -> isWhite(piece) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> isWhite(piece) ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> isWhite(piece) ? WHITE_BISHOP : BLACK_BISHOP;
        };
    }

    private static boolean isWhite(ChessPiece piece){
        return piece.getTeamColor().equals(ChessGame.TeamColor.WHITE);
    }
}
