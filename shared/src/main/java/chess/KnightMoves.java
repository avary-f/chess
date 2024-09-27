package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves extends Moves{
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition pos) {
        int row = pos.getRow();
        int col = pos.getColumn();
        ArrayList<ChessPosition> poses = new ArrayList<>();
        poses.add(new ChessPosition(row + 2, col + 1));
        poses.add(new ChessPosition(row + 2, col - 1));
        poses.add(new ChessPosition(row - 2, col + 1));
        poses.add(new ChessPosition(row - 2, col - 1));
        poses.add(new ChessPosition(row + 1, col + 2));
        poses.add(new ChessPosition(row - 1, col + 2));
        poses.add(new ChessPosition(row + 1, col - 2));
        poses.add(new ChessPosition(row - 1, col - 2));
        for(ChessPosition spot : poses){
            if(attackable(board, pos, spot)){
                add(new ChessMove(pos, spot, null));
            }
        }
        return getMoves();
    }
}
