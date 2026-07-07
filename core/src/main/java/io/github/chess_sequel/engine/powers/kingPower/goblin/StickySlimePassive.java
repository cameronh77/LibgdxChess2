package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Passive goblin king power: slime tiles created by this player stun for 2 turns instead of 1.
 * All SlimeAura creation sites check {@link #isActive} to pick the correct duration.
 */
public class StickySlimePassive extends PassiveKingPower {

    private final boolean isBlackOwned;

    public StickySlimePassive(Piece king) {
        super(king, "Stickier Slime");
        this.isBlackOwned = king.getIsBlack();
    }

    /** Returns true if the given player has Stickier Slime active on this board. */
    public static boolean isActive(Board board, boolean isBlack) {
        for (Aura a : board.getBoardAuras()) {
            if (a instanceof StickySlimePassive && ((StickySlimePassive) a).isBlackOwned == isBlack) return true;
        }
        return false;
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        return moves;
    }

    @Override
    public String getIconPath() { return "kingPowers/stickier-slime.png"; }

    @Override
    public String getDescription() { return "Your slime stuns enemies for 2 turns instead of 1."; }
}
