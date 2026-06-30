package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Castling move. Moves the king two squares toward the rook and places the rook on the
 * other side of the king. Kingside rook lands on column 5; queenside on column 3.
 */
public class Castling extends Move{

    private Piece castle;
    public Castling(Piece piece, int newX, int newY, Board board, Piece castle){
        super(piece, newX, newY, board);
        this.castle = castle;
    }

    @Override
    public void execute(){
        board.getTiles().get(oldX).get(oldY).setPiece(null);
        board.getTiles().get(castle.getCol()).get(castle.getRow()).setPiece(null);

        board.getTiles().get(newX).get(newY).setPiece(movingPiece);
        board.getTiles().get(castle.getCol()==7?5:3).get(castle.getRow()).setPiece(castle);

        movingPiece.setCol(newX);
        movingPiece.setRow(newY);

        castle.setCol(castle.getCol()==7?5:3);
        castle.setRow(castle.getRow());


        movingPiece.setIsFirstMove(false);
        castle.setIsFirstMove(false);

        board.setWhiteToMove(!board.getWhiteToMove());

        board.tick();
    }

    @Override
    public void undo(){
        //Move the kind and castle back
        board.getTiles().get(oldX).get(oldY).setPiece(movingPiece);
        board.getTiles().get(castle.getCol()==5?7:0).get(castle.getRow()).setPiece(castle);

        //Set castle old cords
        castle.setCol(castle.getCol()==5?7:0);
        castle.setRow(castle.getRow());

        //Clear new tiles
        board.getTiles().get(newX).get(newY).setPiece(null);
        board.getTiles().get(castle.getCol()==7?5:3).get(castle.getRow()).setPiece(null);

        //Set king old cords
        movingPiece.setCol(oldX);
        movingPiece.setRow(oldY);

        //Revert first move status
        movingPiece.setIsFirstMove(isFirstMove);
        castle.setIsFirstMove(isFirstMove);

        board.setEnPassantTile(enPassantTile);

        board.setWhiteToMove(!board.getWhiteToMove());

        board.untick();
    }
}
