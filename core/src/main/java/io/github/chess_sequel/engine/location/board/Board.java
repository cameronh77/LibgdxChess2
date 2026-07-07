package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TurnCondition;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.pieces.classic.*;
import io.github.chess_sequel.engine.player.Player;


import java.util.ArrayList;

/**
 * Base class for all board types. Owns the 2-D tile grid, the active piece list,
 * board-wide auras, the currently selected piece, and the turn flag ({@code whiteToMove}).
 * Subclasses specialise for map exploration, combat, team setup, etc.
 */
public abstract class Board {

    public int boardX;
    public int boardY;

    protected ArrayList<Piece> pieces = new ArrayList<>();
    protected int[] enPassantTile = new int[2];
    protected ArrayList<ArrayList<Tile>> tiles = new ArrayList();
    protected ArrayList<Aura> boardAuras = new ArrayList<>();

    protected Piece selectedPiece;
    protected ArrayList<Move> validMoves;

    protected Boolean whiteToMove = false;
    protected Player botPlayer;
    protected TurnCondition turnCondition = null;

    public Board(int boardX, int boardY, Player player, Player opponent){
        this.boardX = boardX;
        this.boardY = boardY;
        for(int c=0; c < boardX; c ++){
            ArrayList<Tile> col = new ArrayList();
            for(int r =0; r < boardY; r++){
                col.add(r, new Tile(c, r));
            }
            tiles.add(c, col);
        }




        //addTestPieces();
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

    /** Adds {@code piece} to the piece list, sets its start coordinates, and places it on the tile. */
    public void addToBoard(Piece piece){
        pieces.add(piece);
        piece.setStartCords();
        tiles.get(piece.getCol()).get(piece.getRow()).setPiece(piece);
    }

    public ArrayList<Aura> getBoardAuras(){
        return boardAuras;
    }

    public void addAura(Aura aura){
        this.boardAuras.add(aura);
    }

    public void removeAura(Aura aura){
        this.boardAuras.remove(aura);
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

    /** Populates {@code validMoves} with legal moves for the currently selected piece (check-aware). */
    public void generatePieceMoves(){
        validMoves = selectedPiece.generateMoves(this, false);
        System.out.println("[GEN_MOVES] " + selectedPiece.getName() + " at (" + selectedPiece.getCol() + "," + selectedPiece.getRow() + ") on " + getClass().getSimpleName() + " -> " + validMoves.size() + " moves, alterMovePowers=" + selectedPiece.getAlterMovePowers().size());
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

    /**
     * Returns {@code true} if executing {@code move} would leave the current side's king
     * in check. Executes and then undoes the move to test.
     */
    public Boolean checkEvaluator(Move move){
        boolean preMoveWhite = whiteToMove;
        move.execute();
        boolean postExecuteWhite = whiteToMove;
        // Always evaluate check from the enemy's perspective so frenzy captures
        // (which don't flip whiteToMove) are still validated for king safety.
        whiteToMove = !preMoveWhite;
        Boolean isKingChecked = false;
        for(Piece piece: pieces){
            ArrayList<Move> moves = piece.generateMoves(this, true);
            for(Move subMove: moves){
                if(subMove.getCapturedPiece() != null && subMove.getCapturedPiece().getName() == "king"){
                    isKingChecked = true;
                }
            }
        }
        whiteToMove = postExecuteWhite;
        move.undo();
        return isKingChecked;
    }



    /** Returns {@code true} if any enemy piece can move to {@code tile} (used to validate castling squares). */
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

    /** Advances all piece-altering power durations by one turn (called at the end of every executed move). */
    public void tick(){
        for(Piece piece: pieces){
            piece.tick();
        }
    }

    /** Reverses a tick — called when a move is undone so durations remain consistent. */
    public void untick(){
        for(Piece piece: pieces){
            piece.untick();
        }
    }

    /** Returns the number of pieces belonging to the given team. */
    public int getTeamPieces(boolean isBlack){
        int value = 0;
        for(Piece piece: pieces){
            if (piece.isBlack()){
                value += 1;
            }
        }
        return value;
    }

    public TurnCondition getTurnCondition() { return turnCondition; }
    public void setTurnCondition(TurnCondition c) { this.turnCondition = c; }

    /** Returns true if a Blood Frenzy is active and at least one unacted piece has a legal move. */
    public boolean hasFrenzyEligibleMoves() {
        if (turnCondition == null) return false;
        for (Piece p : new ArrayList<>(pieces)) {
            if (p.isBlack() == turnCondition.frenzySide && !turnCondition.hasActed(p)) {
                if (!p.generateMoves(this, false).isEmpty()) return true;
            }
        }
        return false;
    }

    public abstract BoardType getBoardType();

}
