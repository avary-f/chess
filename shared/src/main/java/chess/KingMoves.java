package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoves extends Moves{
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition pos) {
        //Collection<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if(i == 0 && j == 0){
                    continue;
                }
                ChessPosition spot = new ChessPosition(row + i, col + j);
                if(attackable(board, pos, spot)){
                    add(new ChessMove(pos, spot, null));
                }
            }
        }
        return getMoves();
    }
}
