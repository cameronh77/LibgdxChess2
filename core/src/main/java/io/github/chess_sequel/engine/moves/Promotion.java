package io.github.chess_sequel.engine.moves;


import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.pieces.classic.Bishop;
import io.github.chess_sequel.engine.pieces.classic.Castle;
import io.github.chess_sequel.engine.pieces.classic.Horse;
import io.github.chess_sequel.engine.pieces.classic.Queen;

/**
 * Pawn promotion move. On execute, replaces the pawn with the chosen piece type (Queen,
 * Bishop, Castle, or Horse) at the promotion square. Undo re-inserts the original pawn.
 */
public class Promotion extends Move{

    private Piece promotionPiece;
    private PieceType type;

    public Promotion(Piece piece, int newX, int newY, Board board, PieceType type){
        super(piece, newX, newY, board);
        this.type = type;

    }

    @Override
    public void execute(){
        board.getTiles().get(oldX).get(oldY).setPiece(null);

        board.getPieces().remove(capturedPiece);
        if(type == PieceType.QUEEN){
            promotionPiece = new Queen(newX, newY, movingPiece.isBlack());
        } else if (type == PieceType.BISHOP){
            promotionPiece = new Bishop(newX, newY, movingPiece.isBlack());
        }else if (type == PieceType.HORSE){
            promotionPiece = new Horse(newX, newY, movingPiece.isBlack());
        }else if (type == PieceType.CASTLE){
            promotionPiece = new Castle(newX, newY, movingPiece.isBlack());
        }

        board.getPieces().remove(movingPiece);
        board.getPieces().add(promotionPiece);

        board.getTiles().get(newX).get(newY).setPiece(promotionPiece);

        promotionPiece.setIsFirstMove(false);

        if (endsTurn()) board.setWhiteToMove(!board.getWhiteToMove());

        board.tick();
    }

    @Override
    public void undo(){
        //Re add initial pawn to tile
        board.getTiles().get(oldX).get(oldY).setPiece(movingPiece);

        //Re add the captured piece
        if(capturedPiece != null){
            board.getPieces().add(capturedPiece);
        }


        board.getPieces().add(movingPiece);
        board.getPieces().remove(promotionPiece);

        board.getTiles().get(newX).get(newY).setPiece(capturedPiece);

        if (endsTurn()) board.setWhiteToMove(!board.getWhiteToMove());

        board.untick();
    }
}
