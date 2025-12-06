package io.github.chess_sequel.engine.location;


import io.github.chess_sequel.engine.pieces.Piece;


public class Tile {
    private int xord;
    private int yord;

    private Piece piece;
    public Tile(int xord, int yord){
        this.xord=xord;
        this.yord=yord;
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
