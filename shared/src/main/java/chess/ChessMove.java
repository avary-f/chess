package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() { //seems like you will only call this if the piece that is being moved is a pawn
        return ChessPiece.PieceType.KING;
//        if(pawnPromotion() == true){
//            return chessPiece.PieceType; // how will it decide which piece type to get? How will it know what you want to promote it to?
//        }
//        else{
//            return null;
//        }
    }

//    public boolean pawnPromotion(){ //is the pawn capable of being promoted
//        if pawn can be promoted{return true;} //how do you know if a pawn can be promoted? - check if its on the end of the board
//        else {return false;}
//    }
}
