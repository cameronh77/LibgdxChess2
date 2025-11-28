package io.github.chess_sequel.engine.location;


import io.github.chess_sequel.engine.pieces.Piece;


public class Tile {
    private int xord;
    private int yord;
    private int size;
    private Piece piece;
    Tile(int xord, int yord, int size){
        this.xord=xord;
        this.yord=yord;
        this.size = size;
    }


    public int getXord() {
        return xord;
    }

    public int getYord(){
        return yord;
    }

    public Piece getPiece(){
        return piece;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }
}
