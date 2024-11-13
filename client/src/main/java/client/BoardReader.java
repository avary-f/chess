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
    private static GameData game;
    private static String playerColor;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 2;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
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

        drawChessBoard(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {
        setBlack(out); // Set background color for the headers

        // Print an empty space at the start for better alignment
        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));

        // Print each column header aligned to the board columns
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, columns.get(boardCol));

            // Add padding space after each header except the last one
            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        // Move to the next line after printing the headers
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(player);
        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRowOfSquares(out);
//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw horizontal row separator.
//                drawHorizontalLine(out);
//                setBlack(out);
//            }
        }
    }

    private static void drawRowOfSquares(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) { // Iterate through board rows
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) { // Iterate through board columns
                // Determine if the square is white or black based on the row and column
                if ((row + col) % 2 == 0) {
                    setWhite(out);  // White square
                } else {
                    setBlack(out);  // Black square
                }

                // Draw the square
                if (row == SQUARE_SIZE_IN_PADDED_CHARS / 2) { // Only print the piece on the middle row of the square
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, row, col); // Print the piece on this square
                    out.print(EMPTY.repeat(suffixLength));
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS)); // Empty space in non-middle rows of square
                }

                // You can optionally add a vertical column separator here if needed, but I've commented it out
                // if (col < BOARD_SIZE_IN_SQUARES - 1) {
                //     setRed(out);
                //     out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                // }

                setBlack(out); // Reset to black for next square
            }
            out.println(); // Move to the next row after finishing the columns
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, int row, int col) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_BLACK);
        String mappedPeice = mappedPiece(game.game.getBoard().getPiece(new ChessPosition(row, col)));
        out.print(mappedPeice);
//        setWhite(out);
    }

    private static String mappedPiece(ChessPiece piece){
        return switch (piece.getPieceType()) {
            case QUEEN -> isWhite(piece) ? WHITE_QUEEN : BLACK_QUEEN;
            case PAWN -> isWhite(piece) ? WHITE_PAWN : BLACK_PAWN;
            case KING -> isWhite(piece) ? WHITE_KING : BLACK_KING;
            case KNIGHT -> isWhite(piece) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> isWhite(piece) ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> isWhite(piece) ? WHITE_BISHOP : BLACK_BISHOP;
            default -> "";
        };
    }

    private static boolean isWhite(ChessPiece piece){
        return piece.getTeamColor().equals(ChessGame.TeamColor.WHITE);
    }
}
