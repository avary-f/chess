package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Moves {
    private Collection<ChessMove> moves;

    public Moves(){
        this.moves = new ArrayList<>();
    }

    public boolean spaceExists(ChessPosition pos){
        return pos.getRow() < 9 && pos.getRow() > 0 && pos.getColumn() < 9 & pos.getColumn() > 0;
    }

    public boolean attackable(ChessBoard board, ChessPosition pos, ChessPosition spot){
        return spaceExists(spot) && (board.getPiece(spot) == null || board.getPiece(spot).getTeamColor() != board.getPiece(pos).getTeamColor());
    }

    public void add(ChessMove move){
        moves.add(move);
    }

    public Collection<ChessMove> getMoves(){
        return moves;
    }

    public abstract Collection<ChessMove> moves(ChessBoard board, ChessPosition pos);
}
