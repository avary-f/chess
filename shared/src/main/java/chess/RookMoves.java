package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves extends Moves {

    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition pos = new ChessPosition(row, col + 1);
        ChessPiece spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ //going right
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(row, pos.getColumn() + 1);
            spot = board.getPiece(pos);
        }
        pos = new ChessPosition(row,  col - 1);
        spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ //going left
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(row, pos.getColumn() - 1);
            spot = board.getPiece(pos);
        }
        pos = new ChessPosition(row + 1, col);
        spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ //going up
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(pos.getRow() + 1, col);
            spot = board.getPiece(pos);
        }
        pos = new ChessPosition(row - 1, col);
        spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)) { //going down
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(pos.getRow() - 1, col);
            spot = board.getPiece(pos);
        }
        return moves;
    }
}
