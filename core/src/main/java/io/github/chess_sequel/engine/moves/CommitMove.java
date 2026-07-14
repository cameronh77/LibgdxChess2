package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.ProphetPathAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.war.strategy.ProphetBishop;

import java.util.ArrayList;

/**
 * Commits the {@link ProphetBishop}'s queued planning steps into the live scry queue,
 * ending the planning turn. The bishop stays in place. Clicking the bishop's own tile
 * while planning triggers this move.
 */
public class CommitMove extends Move {

    private final ProphetBishop bishop;
    private final ArrayList<int[]> snapshot;
    private final ArrayList<ProphetPathAura> markerSnapshot;

    public CommitMove(ProphetBishop bishop, Board board) {
        super(bishop, bishop.getCol(), bishop.getRow(), board);
        this.capturedPiece = null;
        this.bishop = bishop;
        this.snapshot = new ArrayList<>(bishop.planningSteps);
        this.markerSnapshot = new ArrayList<>(bishop.planningMarkers);
    }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();
        bishop.scryQueue.addAll(snapshot);
        bishop.scryMarkers.addAll(markerSnapshot);
        bishop.planningSteps.clear();
        bishop.planningMarkers.clear();
        board.setEnPassantTile(null);
        if (board instanceof MatchBoard) board.setWhiteToMove(!board.getWhiteToMove());
        board.tick();
    }

    @Override
    public void undo() {
        board.untick();
        bishop.planningSteps.addAll(snapshot);
        bishop.planningMarkers.addAll(markerSnapshot);
        bishop.scryQueue.clear();
        bishop.scryMarkers.clear();
        board.setEnPassantTile(enPassantTile);
        if (board instanceof MatchBoard) board.setWhiteToMove(!board.getWhiteToMove());
        board.setTurnCondition(previousCondition);
    }

    @Override
    public boolean endsTurn() { return true; }
}
