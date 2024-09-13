package passoff.chess;

import chess.ChessBoard;
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
        ChessBoard testBoard = new ChessBoard();
        System.out.println(testBoard.toString());
    }

}

