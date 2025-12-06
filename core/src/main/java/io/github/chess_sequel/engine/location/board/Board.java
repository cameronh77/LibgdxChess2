package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;


import java.util.ArrayList;

public abstract class Board {

    public int boardX;
    public int boardY;

    protected ArrayList<Piece> pieces = new ArrayList<>();
    protected int[] enPassantTile = new int[2];
    protected ArrayList<ArrayList<Tile>> tiles = new ArrayList();

    protected Piece selectedPiece;
    protected ArrayList<Move> validMoves;

    protected Boolean whiteToMove = false;
    protected Player botPlayer;

    public Board(int boardX, int boardY, Player opponent){
        this.boardX = boardX;
        this.boardY = boardY;
        for(int c=0; c < boardX; c ++){
            ArrayList<Tile> col = new ArrayList();
            for(int r =0; r < boardY; r++){
                col.add(r, new Tile(c, r));
            }
            tiles.add(c, col);
        }

        botPlayer = opponent;

        addTestPieces();
    }

    public void addTestPieces(){

            addToBoard(new Castle(0, 0, false));
            addToBoard(new Horse(1, 0, false));
            addToBoard(new Bishop(2, 0, false));
            addToBoard(new Queen(3, 0, false));
            addToBoard(new King(4, 0, false));
            addToBoard(new Bishop(5, 0, false));
            addToBoard(new Horse(6, 0, false));
            addToBoard(new Castle(7, 0, false));

            addToBoard(new Pawn(0, 1, false));
            addToBoard(new Pawn(1, 1, false));
            addToBoard(new Pawn(2, 1, false));
            addToBoard(new Pawn(3, 1, false));
            addToBoard(new Pawn(4, 1, false));
            addToBoard(new Pawn(5, 1, false));
            addToBoard(new Pawn(6, 1, false));
            addToBoard(new Pawn(7, 1, false));

            addToBoard(new Pawn(0, 6, true));
            addToBoard(new Pawn(1, 6, true));
            addToBoard(new Pawn(2, 6, true));
            addToBoard(new Pawn(3, 6, true));
            addToBoard(new Pawn(4, 6, true));
            addToBoard(new Pawn(5, 6, true));
            addToBoard(new Pawn(6, 6, true));
            addToBoard(new Pawn(7, 6, true));

            addToBoard(new Castle(0, 7, true));
            addToBoard(new Horse(1, 7, true));
            addToBoard(new Bishop(2, 7, true));
            addToBoard(new Queen(3, 7, true));
            addToBoard(new King(4, 7, true));
            addToBoard(new Bishop(5, 7, true));
            addToBoard(new Horse(6, 7, true));
            addToBoard(new Castle(7, 7, true));

    }

    public void addToBoard(Piece piece){
        pieces.add(piece);
        tiles.get(piece.getCol()).get(piece.getRow()).setPiece(piece);
    }


    public ArrayList<Move> getValidMoves(){
        return validMoves;
    }

    public void resetValidMoves(){
        validMoves = null;
    }

    public ArrayList<ArrayList<Tile>> getTiles(){
        return tiles;
    }

    public void setSelectedPiece(Piece piece){
        this.selectedPiece = piece;
    }

    public Piece getSelectedPiece(){
        return selectedPiece;
    }

    public void generatePieceMoves(){
        validMoves = selectedPiece.generateMoves(this, false);

    }
    public ArrayList<Piece> getPieces() {
        return pieces;
    }
    public Player getBotPlayer(){
        return botPlayer;
    }

    public Boolean getWhiteToMove(){
        return whiteToMove;
    }

    public void setWhiteToMove(Boolean whiteToMove){
        this.whiteToMove = whiteToMove;
    }

    public int[] getEnPassantTile(){
        return enPassantTile;
    }

    public void setEnPassantTile(int[] enPassantTile){
        this.enPassantTile = enPassantTile;
    }

    public Boolean checkEvaluator(Move move){
        move.execute();
        Boolean isKingChecked = false;
        for(Piece piece: pieces){
            //System.out.println(piece);
            ArrayList<Move> moves = piece.generateMoves(this, true);
            for(Move subMove: moves){
                if(subMove.getCapturedPiece() != null && subMove.getCapturedPiece().getName() == "king"){
                    isKingChecked = true;
                }
            }
        }
        move.undo();
        return isKingChecked;
    }



    public Boolean tileCheckEvaluator(Tile tile){
        Boolean isTileChecked = false;
        whiteToMove = !whiteToMove;
        for(Piece piece: pieces){
            if(!(piece.getName()=="king")){
                ArrayList<Move> moves = piece.generateMoves(this, true);
                for(Move subMove: moves){
                    if(subMove.getNewX() == tile.getXord() && subMove.getNewY() == tile.getYord()){
                        isTileChecked = true;
                    }
                }
            }

        }
        whiteToMove = !whiteToMove;
        return isTileChecked;
    }

}
