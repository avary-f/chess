package passoff.chess;

import chess.ChessPosition;
import chess.*;
import org.junit.jupiter.api.Assertions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PersonalTestUtilities {
    static public void validateMoves(ChessBoard board, ChessPiece.PieceType type, ChessPosition startPosition, int[][] endPositions) {
        var testPiece = new ChessPiece(ChessGame.TeamColor.WHITE, type);
        var validMoves = testPiece.pieceMoves(board, startPosition);
        System.out.println(validMoves);
    }
}
