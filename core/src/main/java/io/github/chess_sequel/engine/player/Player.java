package io.github.chess_sequel.engine.player;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.*;

import java.util.ArrayList;

public class Player {

    protected ArrayList<Piece> pieces = new ArrayList<>();
    protected Piece leadPiece;
    private int leadPieceX, leadPieceY;

    private int currency;

    public Player(){
        this.currency = 0;
    }

    public void takeTurn(Board board){

    }



    public void createPieceList(){
        pieces.add(new Castle(0, 0, false));
        pieces.add(new Queen(3, 0, false));
        leadPiece = new King(1, 0, false);
        pieces.add(leadPiece);
        /**
        pieces.add(new Castle(0, 0, false));
        pieces.add(new Horse(1, 0, false));
        pieces.add(new Bishop(2, 0, false));
        pieces.add(new Queen(3, 0, false));
        leadPiece = new King(4, 0, false);
        pieces.add(leadPiece);
        pieces.add(new Bishop(5, 0, false));
        pieces.add(new Horse(6, 0, false));
        pieces.add(new Castle(7, 0, false));

        pieces.add(new Pawn(0, 1, false));
        pieces.add(new Pawn(1, 1, false));
        pieces.add(new Pawn(2, 1, false));
        pieces.add(new Pawn(3, 1, false));
        pieces.add(new Pawn(4, 1, false));
        pieces.add(new Pawn(5, 1, false));
        pieces.add(new Pawn(6, 1, false));
        pieces.add(new Pawn(7, 1, false));
         */
    }

    public ArrayList<Piece> getPieces(){
        return pieces;
    }

    public Piece getLeadPiece(){
        return leadPiece;
    }


    public int getLeadPieceX() {
        return leadPieceX;
    }

    public void setLeadPieceX(int x){
        this.leadPieceX = x;
    }

    public int getLeadPieceY() {
        return leadPieceY;
    }

    public void setLeadPieceY(int y){
        this.leadPieceY = y;
    }

    public int getCurrency(){
        return currency;
    }

    public void setCurrency(int currency){
        this.currency = currency;
    }

    public void incrementCurrency(int currency){
        this.currency += currency;
    }

    public void decrementCurrency(int currency){
        this.currency -= currency;
    }
}
