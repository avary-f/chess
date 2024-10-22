package chess;

import java.util.ArrayList;
import java.util.Collection;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;
public class PawnMoves extends Moves{
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition pos) {
//        Collection<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        int promotionRowWhite = 8;
        int promotionRowBlack = 1;
        if (board.getPiece(pos).getTeamColor() == WHITE) {
            for (int i = -1; i < 2; i++) {
                ChessPosition spot = new ChessPosition(row + 1, col + i);
                getMovesHelper(i, promotionRowWhite, spot, pos, board);
            }
            if (row == 2 && board.getPiece(new ChessPosition(row + 1, col)) == null && board.getPiece(new ChessPosition(row + 2, col)) == null) {
                add(new ChessMove(pos, new ChessPosition(row + 2, col), null));
            }
        }

        else{
            for (int i = -1; i < 2; i++) {
                ChessPosition spot = new ChessPosition(row - 1, col + i);
                getMovesHelper(i, promotionRowBlack, spot, pos, board);
            }
            if (row == 7 && board.getPiece(new ChessPosition(row - 1, col)) == null && board.getPiece(new ChessPosition(row - 2, col)) == null) {
                add(new ChessMove(pos, new ChessPosition(row - 2, col), null));
            }
        }
        return getMoves();
    }
    private void getMovesHelper(int i, int promotionRow, ChessPosition spot, ChessPosition pos, ChessBoard board) {
        ArrayList<ChessPiece.PieceType> types = new ArrayList<>();
        types.add(ROOK);
        types.add(QUEEN);
        types.add(KNIGHT);
        types.add(BISHOP);
        if (!attackable(board, pos, spot)) {
            return;
        }
        if (i == 0 && board.getPiece(spot) == null) {
            if (spot.getRow() == promotionRow) {
                for (ChessPiece.PieceType type : types) {
                    add(new ChessMove(pos, spot, type));
                }
            } else {
                add(new ChessMove(pos, spot, null));
            }
        } else if (i != 0 && board.getPiece(spot) != null && board.getPiece(spot).getTeamColor() != board.getPiece(pos).getTeamColor()) {
            if (spot.getRow() == promotionRow) {
                for (ChessPiece.PieceType type : types) {
                    add(new ChessMove(pos, spot, type));
                }
            } else {
                add(new ChessMove(pos, spot, null));
            }
        }
    }
}

