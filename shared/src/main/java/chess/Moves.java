package chess;

import java.util.Collection;

abstract class Moves {
    public abstract Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor);

    public boolean spaceExists(ChessPosition position){
        return position.getRow() <= 8 && position.getColumn() <= 8 && position.getRow() > 0 && position.getColumn() > 0; //return false if it is an edge
    }
}
