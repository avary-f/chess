package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;
import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;


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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { //collection allows for various return types
        Moves moveList;
        switch (type) {
            case KING -> moveList = new KingMoves();
            case PAWN -> moveList = new PawnMoves();
            case BISHOP -> moveList = new BishopMoves();
            case KNIGHT -> moveList = new KnightMoves();
            case ROOK -> moveList = new RookMoves();
            case QUEEN -> moveList = new QueenMoves();
            default -> throw new RuntimeException("Not implemented");
        };
        return moveList.moves(board, myPosition, pieceColor);
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
        return switch (type) {
            case KING -> (pieceColor == WHITE) ? "K" : "k"; //different syntax to return the result based on if its true of false
            case QUEEN -> (pieceColor == WHITE) ? "Q" : "q";
            case KNIGHT -> (pieceColor == WHITE) ? "N" : "n";
            case ROOK -> (pieceColor == WHITE) ? "R" : "r";
            case PAWN -> (pieceColor == WHITE) ? "P" : "p";
            case BISHOP -> (pieceColor == WHITE) ? "B" : "b";
            case null -> " ";
        };
    }
}
