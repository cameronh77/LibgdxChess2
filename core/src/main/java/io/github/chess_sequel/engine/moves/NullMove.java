package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;

public class NullMove extends Move {
    protected int oldX, oldY;
    protected int newX, newY;
    protected Board board;
    protected Piece movingPiece;
    protected Piece capturedPiece;
    protected Boolean isFirstMove;
    protected int[] enPassantTile;


    public NullMove(Piece piece, int newX, int newY, Board board){
        super(piece, newX, newY, board);
    }

    @Override
    public void execute(){

    }

    @Override
    public void undo(){

    }

    public Piece getMovingPiece(){
        return movingPiece;
    }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }


    public Piece getCapturedPiece(){
        return capturedPiece;
    }
}
