package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;

import java.util.ArrayList;

/**
 * A king power that generates additional moves and costs a charge to use.
 * {@code charges == -1} (or any override of {@link #isAvailable}) means unlimited.
 * Charges are spent on execute and refunded on undo so the minimax search remains consistent.
 */
public abstract class ActiveKingPower implements KingPower {
    protected int charges;

    public boolean isAvailable() { return charges != 0; }
    public boolean isAvailable(Board board) { return isAvailable(); }
    public void spendCharge()    { charges--; }
    public void refundCharge()   { charges++; }

    public abstract String getName();
    public abstract ArrayList<Move> generateMoves(Board board);
}
