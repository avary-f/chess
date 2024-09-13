package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type; //does this need to check if the piece is actually found in PieceType?
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public boolean spaceExists(ChessPosition position){
        return position.getRow() <= 8 && position.getColumn() <= 8; //return false if it is an edge
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0){
                    continue; //will continue onto the next iteration of j of the inner loop if you are evaluating the current position
                }
                int row = myPosition.getRow() + i;
                int col = myPosition.getColumn() + j;
                ChessPosition pos = new ChessPosition(row, col); //position it is trying to go
                if(spaceExists(pos)) { //if the space is on the board
                    ChessPiece spot = board.getPiece(pos); //return the temp chessPiece, null if no piece
                    if(spot == null || spot.pieceColor != pieceColor){ // if the space is empty OR its the other team's piece
                        moves.add(new ChessMove(myPosition, pos, type));
                    }
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow() + 1; //checking the row above the pawn
        for(int j = -1; j <= 1; j++) {
            int col = myPosition.getColumn() + j;
            ChessPosition pos = new ChessPosition(row, col); //position it is trying to go
            if (spaceExists(pos)) { //if the space is on the board
                ChessPiece spot = board.getPiece(pos); //return the temp chessPiece, null if no piece
                if(spot == null){
                    if(j == 0){
                        moves.add(new ChessMove(myPosition, pos, type)); // if its directly in front AND the space is empty
                    }
                }
                else { //only check if the spot is not null to see if it can kill another piece
                    if(j != 0 && spot.pieceColor != pieceColor){ //if it's a diagonal AND the other team's piece
                        moves.add(new ChessMove(myPosition, pos, type)); //need to check if its able to be converted to a new piece?
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { //collection allows for various return types

        return switch (type) {
            case KING -> kingMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
//            case BISHOP -> bishopMoves(board, myPosition);
//            case KNIGHT -> knightMoves(board, myPosition);
//            case ROOK -> rookMoves(board, myPosition);
//            case QUEEN -> queenMoves(board, myPosition);
            default -> throw new RuntimeException("Not implemented");
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
