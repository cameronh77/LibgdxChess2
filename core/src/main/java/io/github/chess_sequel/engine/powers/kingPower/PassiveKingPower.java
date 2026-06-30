package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.pieces.Piece;

public abstract class PassiveKingPower extends Aura implements KingPower {
    public PassiveKingPower(Piece king, String name) {
        super(king, name);
    }
}
