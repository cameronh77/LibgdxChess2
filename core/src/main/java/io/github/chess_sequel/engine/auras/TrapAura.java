package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Board-level aura placed by {@link io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn}.
 * Visible to both players via its imagePath. When an enemy piece lands on the trap tile the pawn
 * teleports there and captures the enemy. Fully undoable via onUndoLand.
 */
public class TrapAura extends Aura {

    private int pawnsOldCol;
    private int pawnsOldRow;
    private boolean wasTriggered = false;

    public TrapAura(Piece owner, int trapCol, int trapRow) {
        super(owner, "trapAura");
        this.auraCol = trapCol;
        this.auraRow = trapRow;
        this.imagePath = "tileModifiers/trap.png";
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        return moves;
    }

    @Override
    public void onLand(Piece piece, int landedX, int landedY, Board board) {
        if (landedX != auraCol || landedY != auraRow) return;
        if (piece.isBlack() == owner.isBlack()) return;

        wasTriggered = true;
        pawnsOldCol = owner.getCol();
        pawnsOldRow = owner.getRow();

        board.getTiles().get(owner.getCol()).get(owner.getRow()).setPiece(null);
        board.getTiles().get(landedX).get(landedY).setPiece(owner);
        owner.setCol(landedX);
        owner.setRow(landedY);
        board.getPieces().remove(piece);
        board.getBoardAuras().remove(this);

        for (Aura aura : new ArrayList<>(board.getBoardAuras())) {
            if (aura instanceof TrapTriggerListener && aura.getOwner() != null
                    && aura.getOwner().isBlack() == owner.isBlack()) {
                ((TrapTriggerListener) aura).onTrapTriggered(owner, auraCol, auraRow, board);
            }
        }
    }

    @Override
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {
        if (!wasTriggered) return;
        if (landedX != auraCol || landedY != auraRow) return;
        if (piece.isBlack() == owner.isBlack()) return;

        wasTriggered = false;
        board.getTiles().get(owner.getCol()).get(owner.getRow()).setPiece(null);
        board.getTiles().get(pawnsOldCol).get(pawnsOldRow).setPiece(owner);
        owner.setCol(pawnsOldCol);
        owner.setRow(pawnsOldRow);
        board.getPieces().add(piece);
        board.getBoardAuras().add(this);
    }
}
