package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves extends Moves{
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor){
        Collection<ChessMove> moves = new ArrayList<>();
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0){
                    continue; //will continue onto the next iteration of j of the inner loop if you are evaluating the current position
                }
                int row = myPosition.getRow() + i;
                int col = myPosition.getColumn() + j;
                ChessPosition pos = new ChessPosition(row, col); //position it is trying to go
                if(spaceExists(pos)) { //if the space is on the board
                    //System.out.println(pos + " exists"); //testing
                    ChessPiece spot = board.getPiece(pos); //return the temp chessPiece, null if no piece
                    if(spot == null || spot.getTeamColor() != pieceColor){ // if the space is empty OR its the other team's piece
                        moves.add(new ChessMove(myPosition, pos, null)); //no promotion piece, so it's null
                    }
                }
            }
        }
        return moves;
    } //I have an issue when it's actually running the test cases!
}
