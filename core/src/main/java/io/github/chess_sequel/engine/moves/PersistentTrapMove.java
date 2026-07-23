package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn;

/**
 * A {@link TrapMove} that does not remove the pawn's previous trap when placing a new one.
 * Used by the Persistence king power so TrapPawns accumulate traps over time.
 */
public class PersistentTrapMove extends TrapMove {

    public PersistentTrapMove(TrapPawn pawn, int trapCol, int trapRow, Board board) {
        super(pawn, trapCol, trapRow, board, true);
    }
}
