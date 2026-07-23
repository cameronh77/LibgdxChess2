package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Implemented by board auras that want to react when a {@link TrapAura} is triggered.
 * Called by TrapAura after it fires, so the listener can place follow-up effects.
 * The trigger coords are provided so the listener can key its undo storage correctly.
 */
public interface TrapTriggerListener {
    void onTrapTriggered(Piece trapOwner, int triggerCol, int triggerRow, Board board);
}
