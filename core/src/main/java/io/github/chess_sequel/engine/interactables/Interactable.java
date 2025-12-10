package io.github.chess_sequel.engine.interactables;

public abstract class Interactable {

    protected int col, row;
    public abstract void interaction();

    public void setCol(int col){
        this.col = col;
    }
    public int getCol(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public int getRow(){
        return row;
    }
}
