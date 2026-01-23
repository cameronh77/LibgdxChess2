package io.github.chess_sequel.engine.pieces;

import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.AlterLayoutMove;
import io.github.chess_sequel.engine.moves.Move;

import java.util.ArrayList;

public abstract class Piece {
    protected String name;
    protected String filePath;
    protected Boolean isFirstMove = true;


    public boolean isWhite() {
        return isWhite;
    }

    protected int col, row; // 0-7
    protected int trueCol, trueRow;
    protected int mapCol, mapRow;
    protected boolean isWhite;

    protected PieceType pieceType;
    protected ChessClass chessClass;

    Piece(int x, int y, boolean isWhite, String name, ChessClass chessClass){
        this.trueCol = x;
        this.trueRow = y;

        this.isWhite = isWhite;
        this.name = name;
        this.chessClass = chessClass;
        this.filePath = "pieces/"+chessClass.getType()+"/"+(isWhite?"black":"white")+"-"+name+".png";
    }


    public ArrayList<Move> generateMoves(Board board, Boolean ignoreCheck){
        return null;
    }

    public ArrayList<Move> generateAlterLayoutMoves(Board board){
        ArrayList<Move> moves = new ArrayList<>();
        for(int x = 0; x< board.boardX;x++){
            for(int y = 0; y < board.boardY/2;y++){
                if(board.getTiles().get(x).get(y).getPiece() == null){
                    moves.add(new AlterLayoutMove(this, x, y, board));
                }
            }
        }
        return moves;
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

    public void setStartCords(){
        //System.out.println("THIS is the true row: "+trueRow + " THis is the true col: "+ trueCol);
        this.row = trueRow;
        this.col = trueCol;
    }

    public void updateStartCords(){
        this.trueRow = row;
        this.trueCol = col;
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

    public int getTrueCol(){
        return trueCol;
    }

    public int getTrueRow(){
        return trueRow;
    }

}
