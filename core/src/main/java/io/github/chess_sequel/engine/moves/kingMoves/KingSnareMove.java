package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.auras.TrapAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

/**
 * Places a {@link TrapAura} owned by the king on a chosen empty tile.
 * When the trap triggers, the king teleports there and captures the enemy —
 * which may leave the king in an inopportune position.
 */
public class KingSnareMove extends KingMove {

    private final int targetCol;
    private final int targetRow;
    private TrapAura trap;

    public KingSnareMove(Piece king, int targetCol, int targetRow, Board board, ActiveKingPower power) {
        super(king, targetCol, targetRow, board, power);
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }

    @Override
    protected void applyEffect() {
        trap = new TrapAura(movingPiece, targetCol, targetRow);
        board.addAura(trap);
    }

    @Override
    protected void revertEffect() {
        board.removeAura(trap);
        trap = null;
    }
}
