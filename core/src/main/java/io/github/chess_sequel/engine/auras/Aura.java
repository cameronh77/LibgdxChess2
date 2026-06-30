package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * An effect that can modify a piece's move list or react to landing events.
 * Auras can be attached to individual tiles or to the board as a whole.
 * Tile-level auras apply only to pieces on that tile; board-level auras apply to every piece.
 */
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

    /** Filters or expands {@code moves} for {@code piece} according to this aura's rules. */
    public abstract ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck);

    /** Called after a piece lands on a tile — used to apply effects triggered by entering a zone. */
    public void onLand(Piece piece, int landedX, int landedY, Board board) {}

    /** Reverses any effect applied in {@link #onLand} when the move is undone. */
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
