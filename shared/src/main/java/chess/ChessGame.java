package chess;
import java.util.Collection;
import static chess.ChessGame.TeamColor.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame{
    private ChessGame.TeamColor turnColor;
    private ChessBoard board = new ChessBoard();
    private boolean inCheck;
    private boolean checkmate;

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
        turnColor = (turnColor == WHITE) ? BLACK : WHITE; //turnColor will be = to whatever (team == white)? if T -> Black
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves= board.getPiece(startPosition).pieceMoves(getBoard(), startPosition);
        inCheck = false;
        for(ChessMove m: validMoves){
            if(m.endPosition().equals(board.getKing(getOtherTeamTurn()))){
                inCheck = true; //it would probably be good to check on it going to check mate in conjunction with being in check
                break;
            }
        }
        return validMoves;
    }
    //I don't think you can allow a king to move somewhere within check, so drop those moves

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.startPosition());
        if(!moves.contains(move)){
            throw new InvalidMoveException("Invalid Move");
        }
        else {
            ChessPiece piece = board.removePiece(move.startPosition());
            board.addPiece(move.endPosition(), piece);
            setTeamTurn(piece.getTeamColor()); //Make it the other team's turn after the piece is moved

            //Check if King is in Check
            Collection<ChessMove> futureMoves = validMoves(move.endPosition());
            for (ChessMove m : futureMoves) {
                if (m.endPosition() == board.getKing(getTeamTurn())) { //check if the king can get killed in next move
                    inCheck = true;
                    break;
                }
                else{
                    inCheck = false;
                }
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {return inCheck;}

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {return checkmate;}

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return validMoves(board.getKing(teamColor)).isEmpty();
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
