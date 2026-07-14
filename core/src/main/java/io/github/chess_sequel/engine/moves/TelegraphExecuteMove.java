package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.TelegraphAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.war.strategy.StrategyQueen;

/**
 * Telegraph-mode second phase: the {@link StrategyQueen} moves to the previously telegraphed
 * destination, capturing any piece there. Removes the {@link TelegraphAura} and switches the
 * queen back to regular movement mode.
 */
public class TelegraphExecuteMove extends Move {

    private final TelegraphAura aura;

    public TelegraphExecuteMove(StrategyQueen queen, int targetCol, int targetRow, TelegraphAura aura, Board board) {
        super(queen, targetCol, targetRow, board);
        this.aura = aura;
    }

    @Override
    public void execute() {
        board.removeAura(aura);
        StrategyQueen queen = (StrategyQueen) movingPiece;
        queen.clearPendingAura();
        queen.leaveTelegraphMode();
        super.execute();
    }

    @Override
    public void undo() {
        super.undo();
        StrategyQueen queen = (StrategyQueen) movingPiece;
        queen.enterTelegraphMode();
        queen.setPendingAura(aura.getAuraCol(), aura.getAuraRow(), aura);
        board.addAura(aura);
    }
}
