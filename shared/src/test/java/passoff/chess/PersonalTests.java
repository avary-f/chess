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
    public void test() {
        ChessPiece pawn = new ChessPiece(WHITE, PAWN);
        ChessPosition pos = new ChessPosition(2, 5);
        ChessBoard testBoard = new ChessBoard(pos, pawn);

        System.out.println(testBoard);
        //testBoard.addPiece(new ChessPosition(5, 8), new ChessPiece(BLACK, QUEEN));
        //System.out.println(testBoard); //print board
        System.out.println(pawn.pieceMoves(testBoard, pos));
    }

}

