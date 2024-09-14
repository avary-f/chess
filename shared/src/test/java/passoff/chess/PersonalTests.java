package passoff.chess;

import static chess.ChessGame.TeamColor.*;
import chess.ChessBoard;
import static chess.ChessPiece.PieceType.*;

import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Test;

import static passoff.chess.PersonalTestUtilities.validateMoves;

public class PersonalTests {

    @Test
    public void kingMoveUntilEdge() {
//        validateMoves("""
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | | | |K| | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        """,
//                new ChessPosition(3, 6),
//                new int[][]{{4, 6}, {4, 7}, {3, 7}, {2, 7}, {2, 6}, {2, 5}, {3, 5}, {4, 5}}
//        );
        ChessPiece pawn = new ChessPiece(BLACK, PAWN);
        ChessPosition pos = new ChessPosition(2, 3);
        ChessBoard testBoard = new ChessBoard(pos, pawn);
        //testBoard.addPiece(new ChessPosition(5, 8), new ChessPiece(BLACK, QUEEN));
        //System.out.println(testBoard); //print board
        System.out.println(pawn.pieceMoves(testBoard, pos));
    }

}

