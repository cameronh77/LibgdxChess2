package io.github.chess_sequel.engine.powers.kingPower;

public interface KingPower {
    String getName();
    default String getIconPath() { return null; }
    default String getDescription() { return null; }
}
