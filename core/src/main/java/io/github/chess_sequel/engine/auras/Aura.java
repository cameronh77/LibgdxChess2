package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

public abstract class Aura {

    protected Piece owner;
    protected String name;
    protected String imagePath = null;
    protected int auraCol = -1;
    protected int auraRow = -1;

    public Aura(Piece owner, String name){
        this.owner = owner;
        this.name = name;
    }

    public abstract ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck);

    public void onLand(Piece piece, int landedX, int landedY, Board board) {}
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {}

    public Piece getOwner(){
        return owner;
    }

    public String getName(){
        return name;
    }

    public String getImagePath() { return imagePath; }
    public int getAuraCol() { return auraCol; }
    public int getAuraRow() { return auraRow; }
}
