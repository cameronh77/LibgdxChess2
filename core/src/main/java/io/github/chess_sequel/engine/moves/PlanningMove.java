package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.ProphetPathAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.war.strategy.ProphetBishop;

/**
 * One planning step queued on a {@link ProphetBishop}. Does not move the bishop —
 * adds a {@link ProphetPathAura} marker at the target and records the step in the
 * bishop's planning list. Does not end the turn.
 */
public class PlanningMove extends Move {

    private final ProphetBishop bishop;
    private final ProphetPathAura marker;

    public PlanningMove(ProphetBishop bishop, int targetCol, int targetRow, Board board) {
        super(bishop, targetCol, targetRow, board);
        this.capturedPiece = null;
        this.bishop = bishop;
        this.marker = new ProphetPathAura(bishop, targetCol, targetRow);
    }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();
        bishop.planningSteps.add(new int[]{newX, newY});
        bishop.planningMarkers.add(marker);
        board.addAura(marker);
        board.setEnPassantTile(null);
        board.tick();
    }

    @Override
    public void undo() {
        board.untick();
        bishop.planningSteps.remove(bishop.planningSteps.size() - 1);
        bishop.planningMarkers.remove(bishop.planningMarkers.size() - 1);
        board.removeAura(marker);
        board.setEnPassantTile(enPassantTile);
        board.setTurnCondition(previousCondition);
    }

    @Override
    public boolean endsTurn() { return false; }
}
