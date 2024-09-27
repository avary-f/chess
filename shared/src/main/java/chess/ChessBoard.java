package chess; //does this mean that it is pulling in all the other files in the chess folder?
//doesn't have anything to do with subclasses

import java.util.*;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    public ChessBoard(ChessPosition pos, ChessPiece piece){ //constructor that takes one piece and constructs an empty board
        addPiece(pos, piece);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        board[row - 1][col - 1] = piece;
    }

    public ChessPiece removePiece(ChessPosition position){
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece piece = board[r - 1][c - 1];
        board[r - 1][c - 1] = null;
        return piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    } //returns null if nothing is there

    public void setPawns(){
        for(int j = 1; j < 9; j++) { //fill up all columns
            addPiece(new ChessPosition(2, j), new ChessPiece(WHITE, PAWN));
            addPiece(new ChessPosition(7, j), new ChessPiece(BLACK, PAWN));
        }
    }
    public void setOthers(){
        // Set Rooks
        addPiece(new ChessPosition(1, 1), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(BLACK, ROOK));

        // Set Knights
        addPiece(new ChessPosition(1, 2), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(BLACK, KNIGHT));

        //Set Bishops
        addPiece(new ChessPosition(1, 3), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(BLACK, BISHOP));

        //Set Kings
        addPiece(new ChessPosition(1, 5), new ChessPiece(WHITE, KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(BLACK, KING));

        //Set Queens
        addPiece(new ChessPosition(1, 4), new ChessPiece(WHITE, QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(BLACK, QUEEN));
    }

    // gets the king of specified color and if it doesn't exits, return null
    public ChessPosition getKing(ChessGame.TeamColor color){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition pos = new ChessPosition(i, j);
                if (getPiece(pos).getPieceType() == KING && getPiece(pos).getTeamColor() == color) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = null;
            }
        }
        setPawns();
        setOthers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }

    @Override
    public String toString() {
        String layout = "";

        for (int i = 8; i > 0; i--) {  // Loop through each row from 8 to 1 (to print from top to bottom)
            for (int j = 1; j < 9; j++) {  // Loop through each column from 1 to 8
                ChessPosition pos = new ChessPosition(i, j);  // Create the position
                ChessPiece piece = getPiece(pos);  // Get the piece at this position

                if (piece != null) {
                    layout += "|" + piece.toString();  // If there's a piece, add its string representation
                } else {
                    layout += "| ";  // If no piece, add an empty space
                }
            }
            layout += "|\n";  // Add the end of the row
        }

        return layout;
    }

}
