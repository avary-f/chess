package chess;

import java.util.ArrayList;
import java.util.Collection;


public class QueenMoves extends Moves {

    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition pos) {
        Moves bishop = new BishopMoves();
        bishop.moves(board, pos);
        Moves rook = new RookMoves();
        rook.moves(board, pos);
        for (ChessMove move : bishop.getMoves()) {
            add(move);
        }
        for (ChessMove move : rook.getMoves()) {
            add(move);
        }
        return getMoves();
    }
}