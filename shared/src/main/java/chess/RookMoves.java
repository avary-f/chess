package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoves extends Moves{

    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition pos) {
        int row = pos.getRow();
        int col = pos.getColumn();
        for (int i = 1; i < 9; i++) {
            ChessPosition spot = new ChessPosition(row + i, col);
            if (attackable(board, pos, spot)) {
                add(new ChessMove(pos, spot, null));
                if(board.getPiece(spot) != null){
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 9; i++) {
            ChessPosition spot = new ChessPosition(row - i, col);
            if (attackable(board, pos, spot)) {
                add(new ChessMove(pos, spot, null));
                if(board.getPiece(spot) != null){
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 9; i++) {
            ChessPosition spot = new ChessPosition(row, col - i);
            if (attackable(board, pos, spot)) {
                add(new ChessMove(pos, spot, null));
                if(board.getPiece(spot) != null){
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 9; i++) {
            ChessPosition spot = new ChessPosition(row, col + i);
            if (attackable(board, pos, spot)) {
                add(new ChessMove(pos, spot, null));
                if(board.getPiece(spot) != null){
                    break;
                }
            } else {
                break;
            }
        }
        return getMoves();
    }
}
