package io.github.chess_sequel.engine.player;

import io.github.chess_sequel.engine.interactables.ConsumableItem;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.pieces.classic.Pawn;
import io.github.chess_sequel.engine.powers.kingPower.classic.BouncingBishopsPassive;
import io.github.chess_sequel.engine.powers.kingPower.classic.MeekInheritPower;
import io.github.chess_sequel.engine.powers.kingPower.goblin.WinBonus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the human player. Holds the active piece team, the king piece used as the
 * lead on the map, a piece inventory (items picked up from shops), currency, and the
 * saved map position of the lead piece between boards.
 */
public class Player {

    protected ArrayList<Piece> pieces = new ArrayList<>();
    protected Piece leadPiece;
    private int leadPieceX, leadPieceY;

    private int currency;
    private ArrayList<Piece> pieceInventory = new ArrayList<>();
    private ArrayList<ConsumableItem> consumables = new ArrayList<>();
    private String playerClass = "classic";

    public Player(){
        this.currency = 30;
    }

    public String getPlayerClass() { return playerClass; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }

    public void takeTurn(Board board){

    }



    public void createPieceList(){
        pieces.add(new Pawn(1, 0, false));
        pieces.add(new Pawn(2, 0, false));
        pieces.add(new Pawn(3, 0, false));
        King king = new King(0, 0, false);
        leadPiece = king;
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

    /** Replaces the current piece list and sets {@code leadPiece} to the first King found. */
    public void setTeam(List<Piece> pieces) {
        this.pieces = new ArrayList<>(pieces);
        this.leadPiece = null;
        for (Piece piece : this.pieces) {
            if (piece instanceof King) {
                this.leadPiece = piece;
                break;
            }
        }
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

    public ArrayList<Piece> getPieceInventory(){
        return pieceInventory;
    }

    public ArrayList<ConsumableItem> getConsumables() { return consumables; }

    public King getKing() {
        for (Piece piece : pieces) {
            if (piece instanceof King) return (King) piece;
        }
        return null;
    }

}
