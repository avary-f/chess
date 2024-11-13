package client;
import model.GameData;
import static ui.EscapeSequences.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class BoardReader { //prints out the board
    private GameData game;
    private String playerColor;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    private static Random rand = new Random();

    public BoardReader(GameData game, String playerColor) {
        this.game = game;
        this.playerColor = playerColor;
    }
    private static void drawHeaders(PrintStream out) {

        //setBlack(out);

        String[] headers = { "TIC", "TAC", "TOE" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

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

        //setBlack(out);
    }


    private static String[][] initializeBoard() {
        String[][] board = new String[8][8];

        board[0] = new String[]{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        board[1] = new String[]{BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN};

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = " ";
            }
        }

        board[6] = new String[]{WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN};
        board[7] = new String[]{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};

        return board;
    }

    // Method to create a colored grid and return it as a string
    public String printBoard() {
        StringBuilder gridString = new StringBuilder();

        // Loop through each row of the board
        for (int row = 7; row >= 0; row--) {
            gridString.append(row + 1).append(" ");  // Add the row number

            // Loop through each column of the row
            for (int col = 0; col < 8; col++) {
                // Alternate between black and white squares using Unicode block characters
                if ((row + col) % 2 == 0) {
                    gridString.append("\u2591\u2591");  // White square (light block)
                } else {
                    gridString.append("\u2588\u2588");  // Black square (solid block)
                }
            }
            gridString.append("\n");  // End the row
        }

        // Add a footer with the column labels again
        gridString.append("  a b c d e f g h\n");

        return gridString.toString();  // Return the full grid string
    }
}
