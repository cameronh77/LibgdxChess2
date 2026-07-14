package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * A non-movement action targeting an impassable interactable (e.g. an NPC).
 * The piece stays in place; the interaction fires via the normal BoardInput path
 * after execute() returns. Only board.tick() is called so NPCs still update.
 */
public class InteractMove extends Move {

    public InteractMove(Piece piece, int targetCol, int targetRow, Board board) {
        super(piece, targetCol, targetRow, board);
    }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();
        board.tick();
    }

    @Override
    public void undo() {
        board.untick();
        board.setTurnCondition(previousCondition);
    }
}
