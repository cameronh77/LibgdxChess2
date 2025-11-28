package io.github.chess_sequel.engine.pieces;

import io.github.chess_sequel.engine.location.Board;
import io.github.chess_sequel.engine.moves.Move;

import java.util.ArrayList;

public abstract class Piece {
    protected String name;
    protected String filePath;
    protected Boolean isFirstMove = true;


    public boolean isWhite() {
        return isWhite;
    }


    protected int xord;
    protected int yord;

    protected int col, row; // 0-7
    protected boolean isWhite;
    protected int size;

    protected PieceType pieceType;

    Piece(int x, int y, boolean isWhite, String name, int size){
        this.col = x;
        this.row = y;
        this.xord = x*size;
        this.yord = y*size;
        this.isWhite = isWhite;
        this.name = name;
        this.size = size;
        this.filePath = "pieces/"+(isWhite?"black":"white")+"-"+name+".png";
    }


    public ArrayList<Move> generateMoves(Board board, Boolean ignoreCheck){
        return null;
    }

    public void setIsFirstMove(Boolean state){
        isFirstMove = state;
    }

    public Boolean getIsFirstMove(){
        return isFirstMove;
    }

    public Boolean getIsWhite(){
        return isWhite;
    }

    public void setCol(int col){
        this.col = col;
        this.xord = col*size;
    }
    public int getCol(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
        this.yord = row*size;
    }

    public int getRow(){
        return row;
    }

    public String getName(){
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public PieceType getPieceType(){
        return pieceType;
    }
}
