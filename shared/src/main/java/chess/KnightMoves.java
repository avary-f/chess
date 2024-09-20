package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves extends Moves{
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition pos;
        ChessPiece spot;
        for(int i = -2; i < 3; i++) { //looking at two rows down (-2) and 2 rows up (+2)
            if (i == 0) {
                continue;
            }
            if (i == -1 || i == 1) { //if the rows are -1 or +1
                for (int j = -2; j < 3; j = j + 4) { //looking at col left (-2) and col right (+2)
                    pos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
                    spot = board.getPiece(pos);
                    if (spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)) {
                        moves.add(new ChessMove(myPosition, pos, null));
                    }
                }
            } else { //if the rows are -2 or +2
                for (int j = -1; j < 2; j++) { //looking at col left (-1) and col right (+1)
                    if (j == 0) {
                        continue;
                    }
                    pos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
                    spot = board.getPiece(pos);
                    if (spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)) {
                        moves.add(new ChessMove(myPosition, pos, null));
                    }
                }
            }
        }
        return moves;
    }
}
