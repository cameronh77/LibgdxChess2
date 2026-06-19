package io.github.chess_sequel.engine.powers.pieceAltering;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

public abstract class AlterMovePower {

    protected ArrayList<PieceType> inheritors; //The pieces that can receive this power
    protected int duration;
    protected boolean removeAtGameEnd;

    public AlterMovePower(ArrayList<PieceType> inheritors, int duration){
        this.inheritors = inheritors;
        this.duration = duration;
    }

    public abstract ArrayList<Move> alterMoves(ArrayList<Move> moves, Board board, Boolean isCheck);

    public void tick(){
        duration -= 1;
    }

    public void untick(){
        duration += 1;
    }

    public int getDuration() {
        return duration;
    }

    public boolean getRemoveAtGameEnd(){
        return removeAtGameEnd;
    }
}
