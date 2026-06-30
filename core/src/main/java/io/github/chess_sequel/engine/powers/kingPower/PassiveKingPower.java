package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * A king power that acts as a persistent board aura for the entire match.
 * It is registered via {@link io.github.chess_sequel.engine.pieces.classic.King#onStart} and
 * removed when the king is captured.
 */
public abstract class PassiveKingPower extends Aura implements KingPower {
    public PassiveKingPower(Piece king, String name) {
        super(king, name);
    }
}
