package chess; //does this mean that it is pulling in all the other files in the chess folder?
//doesn't have anything to do with subclasses

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private Map<ChessPosition, ChessPiece> board = new HashMap<>();

    public ChessBoard() {
        resetBoard(); //set the board upon construction
    }

    public ChessBoard(ChessPosition pos, ChessPiece piece){
        addPiece(pos, piece);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board.get(position);
    }

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


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board.clear();
        setPawns();
        setOthers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }

    @Override
    public String toString() {
        String layout = "";
        boolean match;
        for(int i = 1; i < 9; i++){ //loop through each row
            for(int j = 1; j < 9; j++){ //loop through each column
                match = false; //reset match for each spot
                for(ChessPosition pos: board.keySet()){ //get access to each position
                    if(pos.getRow() == i && pos.getColumn() == j) { //see if it matched with current position
                        layout += "|" + board.get(pos).toString2() ; //if it matches, add it (toString2 is for the singular letter)
                        match = true; //found match for that spot
                        break;
                    }
                }
                if(!match) {
                    layout += "| "; //if it didn't find a match for that spot, add an empty char
                }
            }
            match = false;
            layout += "|\n";
        }

        return layout;
    }
}
