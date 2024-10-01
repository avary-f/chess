package chess;
import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame{
    private TeamColor turnColor = WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {}

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnColor;
    }

    public TeamColor getOtherTeamTurn() {return (turnColor == WHITE) ? BLACK : WHITE;}

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnColor = team; //turnColor will be = to whatever (team == white)? if T -> Black
    }

    public Collection<ChessPosition> getOpponentPieces(ChessPiece piece){
        Collection<ChessPosition> opponents= new ArrayList<>();
        for(int r = 1; r < 9; r++){
            for(int c = 1; c < 9; c++){ //iterate through the board
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece oppPiece = board.getPiece(pos); //possible opponent piece
                if(oppPiece != null && oppPiece.getTeamColor() != piece.getTeamColor()){ //check if it's opponent
//                    System.out.print("Adding an opponent: "); //testing
//                    System.out.print(oppPiece); //testing
                    opponents.add(pos); //if it is, add it to the list
                }
            }
        }
        return opponents;
    }
    public Collection<ChessPosition> getMyTeamPieces(ChessPiece piece){
        Collection<ChessPosition> myTeam= new ArrayList<>();
        for(int r = 1; r < 9; r++){
            for(int c = 1; c < 9; c++){ //iterate through the board
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece myTeamPiece = board.getPiece(pos); //possible opponent piece
                if(myTeamPiece != null && myTeamPiece.getTeamColor().equals(piece.getTeamColor())){ //check if it's opponent
//                    System.out.print("Adding an teams piece: "); //testing
//                    System.out.print(myTeamPiece); //testing
                    myTeam.add(pos); //if it is, add it to the list
                }
            }
        }
        return myTeam;
    }

    public boolean goodMove(ChessMove move){
        ChessBoard tempBoard = getBoard();
        ChessPiece piece = tempBoard.removePiece(move.getStartPosition()); //make temp move
        //System.out.println(piece);
        if(piece == null){
            return false;
        }
        tempBoard.addPiece(move.getEndPosition(), piece);
        return !isInCheck(piece.getTeamColor());  //return if move puts king in check
    }

    public Collection<ChessMove> dropInvalidMoves(Collection<ChessMove> pieceMoves, ChessPiece piece){
        //Collection<ChessPosition> opponents = getOpponentPieces(piece); //get each opponent position
        Collection<ChessMove> validMoves = new ArrayList<>(); //only add valid moves
        for(ChessMove move: pieceMoves){   //go through each move the piece can make
            if(goodMove(move)){  //if that move won't result in the king being in check
                //System.out.print("Adding a valid king move: "); //testing
                //System.out.println(move); //testing
                validMoves.add(move);
            }
            else{
//                System.out.print("King can't move here: ");
//                System.out.println(move);
            }
        }
        if(validMoves.isEmpty()){
            return null;
        }
        return validMoves;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null) { //return null if no piece at startPosition
            return null;
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(getBoard(), startPosition);
        allMoves = dropInvalidMoves(allMoves, piece); //drop invalid moves for every type of piece.
        //you can never end your turn with your king in check!
        return allMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if(moves == null || moves.isEmpty() || !moves.contains(move)){
            throw new InvalidMoveException("Invalid Move");
        }
        else {
//            System.out.print("making a move: "); //testing
//            System.out.println(move); //testing
            ChessPiece piece = board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            setTeamTurn(getOtherTeamTurn()); //Make it the other team's turn after the piece is moved, not sure if I need this yet
        }
    }

    public boolean canAttack(ChessPosition oppPos, ChessPosition kingPos){
        ChessPiece oppPiece = board.getPiece(oppPos);
        Collection<ChessMove> oppMoves = oppPiece.pieceMoves(board, kingPos); //check what the opponent can do
        for(ChessMove oppMove: oppMoves){
            if(oppMove.getEndPosition().equals(kingPos)){ //if the opponent can kill the king
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = board.getKing(teamColor); //get king's pos
//        System.out.print("King pos: "); //testing
//        System.out.println(kingPos); //testing
        //System.out.println(board); //testing
        ChessPiece king = board.getPiece(kingPos); //get the king piece
        Collection<ChessPosition> opponents = getOpponentPieces(king); //get each opponent position
        for(ChessPosition oppPos: opponents){
            if(canAttack(oppPos, kingPos)){
                return true;
            }
        }
        return false;
    }

    public Collection<ChessMove> getTotalTeamMoves(TeamColor teamColor){
        ChessPosition kingPos = board.getKing(teamColor); //get king position we are checking
        ChessPiece king = board.getPiece(kingPos); //get the king object
        Collection<ChessMove> allMoves = new ArrayList<>();
        Collection<ChessPosition> myTeam = getMyTeamPieces(king); //all the pieces on your team
        for(ChessPosition myGuy: myTeam){
            Collection<ChessMove> myGuyMoves = validMoves(myGuy); //check what your guy can do
            if(!myGuyMoves.isEmpty()){
                allMoves.addAll(myGuyMoves); //if there are moves they can do, add them to the list
            }
        }
        return allMoves;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && getTotalTeamMoves(teamColor).isEmpty(); //check if the king is in check & if no other pieces can do anything
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && getTotalTeamMoves(teamColor).isEmpty(); //check if the king is NOT in check & if no pieces can do anything
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {this.board = board;}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {return board;}
}
