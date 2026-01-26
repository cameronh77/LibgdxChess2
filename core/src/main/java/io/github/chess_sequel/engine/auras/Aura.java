package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

public abstract class Aura {

    protected Piece owner;
    protected String name;
    public Aura(Piece owner, String name){
        this.owner = owner;
        this.name = name;
    }

    public abstract ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck);

    public Piece getOwner(){
        return owner;
    }

    public String getName(){
        return name;
    }
}
