package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.PieceType.KNIGHT;

public class PawnMoves extends Moves{
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor){
        ArrayList<ChessPiece.PieceType> promotionPieces = new ArrayList<>();
        promotionPieces.add(QUEEN); promotionPieces.add(BISHOP); promotionPieces.add(ROOK); promotionPieces.add(KNIGHT);
        Collection<ChessMove> moves = new ArrayList<>();
        int row;
        if(pieceColor == WHITE){
            row = myPosition.getRow() + 1; //checking the row above the white pawn
        }
        else{
            row = myPosition.getRow() - 1; //checking the row above the black pawn
        }
        for(int j = -1; j <= 1; j++) {
            int col = myPosition.getColumn() + j;
            ChessPosition pos = new ChessPosition(row, col); //position it is trying to go
            if (spaceExists(pos)) { //if the space is on the board
                ChessPiece spot = board.getPiece(pos); //return the temp chessPiece, null if no piece
                if(spot == null){
                    if(j == 0){
                        if((row == 8 && pieceColor == WHITE) || (row == 1 && pieceColor == BLACK)){
                            for(ChessPiece.PieceType piece : promotionPieces){ //gets all the possible promotion pieces
                                moves.add(new ChessMove(myPosition, pos, piece));
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, pos, null)); // if its directly in front AND the space is empty
                        }
                    }
                }
                else { //only check if the spot is not null to see if it can kill another piece
                    if(j != 0 && spot.getTeamColor() != pieceColor){ //if it's a diagonal AND the other team's piece
                        if((row == 8 && pieceColor == WHITE) || (row == 1 && pieceColor == BLACK)){
                            for(ChessPiece.PieceType piece : promotionPieces){
                                moves.add(new ChessMove(myPosition, pos, piece)); //gets all the possible promotion pieces
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, pos, null)); // if its directly in front AND the space is empty
                        }
                        //moves.add(new ChessMove(myPosition, pos, null)); // TO DO: need to check if its able to be converted to a new piece?
                    }
                }
            }
        }
        if(myPosition.getRow() == 2 && pieceColor == WHITE && board.getPiece(new ChessPosition(4, myPosition.getColumn())) == null
                && board.getPiece(new ChessPosition(3, myPosition.getColumn())) == null){
            moves.add(new ChessMove(myPosition, new ChessPosition(4, myPosition.getColumn()), null));
        }
        else if(myPosition.getRow() == 7 && pieceColor == BLACK && board.getPiece(new ChessPosition(5, myPosition.getColumn())) == null
                && board.getPiece(new ChessPosition(6, myPosition.getColumn())) == null){
            moves.add(new ChessMove(myPosition, new ChessPosition(5, myPosition.getColumn()), null));
        }
        //System.out.println(board);
        return moves;
    }
}
