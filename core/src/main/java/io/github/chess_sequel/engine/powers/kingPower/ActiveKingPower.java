package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;

import java.util.ArrayList;

public abstract class ActiveKingPower implements KingPower {
    protected int charges;

    public boolean isAvailable() { return charges != 0; }
    public boolean isAvailable(Board board) { return isAvailable(); }
    public void spendCharge()    { charges--; }
    public void refundCharge()   { charges++; }

    public abstract String getName();
    public abstract ArrayList<Move> generateMoves(Board board);
}
