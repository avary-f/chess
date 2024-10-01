package chess;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Moves moveObj;
        switch (type){
            case KING -> moveObj = new KingMoves();
            case QUEEN -> moveObj = new QueenMoves();
            case PAWN -> moveObj = new PawnMoves();
            case BISHOP -> moveObj = new BishopMoves();
            case ROOK -> moveObj = new RookMoves();
            case KNIGHT -> moveObj = new KnightMoves();
            default -> throw new RuntimeException("Invalid Piece");
        }
        return moveObj.moves(board, myPosition);

    }

    @Override
    public String toString() {
        return switch (type) {
            case KING -> (pieceColor == WHITE) ? "K" : "k"; //different syntax to return the result based on if its true of false
            case QUEEN -> (pieceColor == WHITE) ? "Q" : "q";
            case KNIGHT -> (pieceColor == WHITE) ? "N" : "n";
            case ROOK -> (pieceColor == WHITE) ? "R" : "r";
            case PAWN -> (pieceColor == WHITE) ? "P" : "p";
            case BISHOP -> (pieceColor == WHITE) ? "B" : "b";
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
}
