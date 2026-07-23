package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;

/**
 * Pre-game strategy king power: each TrapPawn on the player's team enters the match
 * with a trap already placed on one of its forward diagonal tiles.
 */
public class DeadfallPrePower extends PreKingPower {

    public DeadfallPrePower(Piece owner) {}

    @Override
    public void apply(AlterLayoutBoard board) {
        for (Piece piece : board.getPieces()) {
            if (piece instanceof TrapPawn) {
                ((TrapPawn) piece).setDeadfallActive(true);
            }
        }
    }

    @Override
    public String getName() { return "Deadfall"; }

    @Override
    public String getIconPath() { return "kingPowers/deadfall.png"; }

    @Override
    public String getDescription() { return "Each TrapPawn begins the match with a trap already set."; }
}
