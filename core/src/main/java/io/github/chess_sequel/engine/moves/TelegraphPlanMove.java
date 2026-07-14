package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.TelegraphAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.StrategyQueen;

/**
 * Telegraph-mode first phase: the {@link StrategyQueen} selects a destination without moving.
 * Places a {@link TelegraphAura} on the target tile so both players can see it.
 * The queen executes the actual move on her next turn via {@link TelegraphExecuteMove}.
 */
public class TelegraphPlanMove extends Move {

    private final TelegraphAura aura;

    public TelegraphPlanMove(StrategyQueen queen, int targetCol, int targetRow, Board board) {
        super(queen, queen.getCol(), queen.getRow(), board);
        this.newX = targetCol;
        this.newY = targetRow;
        this.aura = new TelegraphAura(queen, targetCol, targetRow);
    }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();

        StrategyQueen queen = (StrategyQueen) movingPiece;
        queen.setPendingAura(newX, newY, aura);
        board.addAura(aura);

        board.setEnPassantTile(null);
        if (board instanceof MatchBoard && endsTurn()) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }
        board.tick();
    }

    @Override
    public void undo() {
        board.untick();

        StrategyQueen queen = (StrategyQueen) movingPiece;
        board.removeAura(aura);
        queen.clearPendingAura();

        board.setEnPassantTile(enPassantTile);
        if (board instanceof MatchBoard && endsTurn()) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }
        board.setTurnCondition(previousCondition);
    }

    @Override
    public boolean endsTurn() { return true; }
}
