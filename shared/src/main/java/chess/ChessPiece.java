package chess;

import java.util.Collection;
import java.util.ArrayList;

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
                    ChessPiece spot = board.getPiece(pos); //create a temp chessPiece, null if no piece
                    if(spot == null || spot.pieceColor != pieceColor){ // if the space is empty OR its the other team's piece
                        moves.add(new ChessMove(myPosition, pos, type));
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

        switch(type) {
            case KING:
                return kingMoves(board, myPosition);
//            case QUEEEN:
//                break;
//            case BISHOP:
//
//                break;
//            case KNIGHT:
//
//                break;
//            case ROOK:
//
//                break;
//            case PAWN:
//
//                break;
        }
        throw new RuntimeException("Not implemented");
    }
}
