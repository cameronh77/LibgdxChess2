package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Move used on the {@link io.github.chess_sequel.engine.location.board.AlterLayoutBoard}.
 * Repositions a piece to any empty tile in the bottom half of the board and updates its
 * true start coordinates. Undo is a no-op since layout edits are permanent.
 */
public class AlterLayoutMove extends Move{

    public AlterLayoutMove(Piece piece, int newX, int newY, Board board){
        super(piece, newX, newY, board);
    }

    @Override
    public void execute() {

        if(!board.getPieces().contains(movingPiece)){
            board.getPieces().add(movingPiece);
        }
        //Vacate old piece
        board.getTiles().get(oldX).get(oldY).setPiece(null);

        //Remove captured piece from the board
        board.getPieces().remove(capturedPiece);

        //Add the piece being moved to the new tile
        board.getTiles().get(newX).get(newY).setPiece(movingPiece);

        //Set the moving pieces location
        movingPiece.setCol(newX);
        movingPiece.setRow(newY);

        movingPiece.updateStartCords();

        //Alter first move state
        movingPiece.setIsFirstMove(false);

    }

    @Override
    public void undo(){

    }
}
