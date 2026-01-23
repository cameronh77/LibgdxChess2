package io.github.chess_sequel.engine.powers.pieceAltering;

import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

public abstract class AlterMovePower {

    protected ArrayList<PieceType> inheritors; //The pieces that can receive this power

    public AlterMovePower(){

    }

    public ArrayList<Move> alterMoves(){
        return null;
    }
}
