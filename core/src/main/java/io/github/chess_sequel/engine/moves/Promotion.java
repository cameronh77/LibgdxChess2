package io.github.chess_sequel.engine.moves;


import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.*;

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
            promotionPiece = new Queen(newX, newY, movingPiece.isWhite());
        } else if (type == PieceType.BISHOP){
            promotionPiece = new Bishop(newX, newY, movingPiece.isWhite());
        }else if (type == PieceType.HORSE){
            promotionPiece = new Horse(newX, newY, movingPiece.isWhite());
        }else if (type == PieceType.CASTLE){
            promotionPiece = new Castle(newX, newY, movingPiece.isWhite());
        }

        board.getPieces().remove(movingPiece);
        board.getPieces().add(promotionPiece);

        board.getTiles().get(newX).get(newY).setPiece(promotionPiece);

        promotionPiece.setIsFirstMove(false);

        board.setWhiteToMove(!board.getWhiteToMove());
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

        board.setWhiteToMove(!board.getWhiteToMove());
    }
}
