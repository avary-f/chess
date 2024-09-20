package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves extends Moves{
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition pos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        ChessPiece spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ //left upper diagonal
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1);
            spot = board.getPiece(pos);

        }
        pos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ // right upper diagonal
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1);
            spot = board.getPiece(pos);

        }
        pos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ //left lower diagonal
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }pos = new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1);
            spot = board.getPiece(pos);

        }
        pos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        spot = board.getPiece(pos);
        while(spaceExists(pos) && (spot == null || spot.getTeamColor() != pieceColor)){ //right lower diagonal
            moves.add(new ChessMove(myPosition, pos, null));
            if(spot != null){
                break;
            }
            pos = new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1);
            spot = board.getPiece(pos);

        }
        return moves;
    }
}
