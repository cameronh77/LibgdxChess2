package io.github.chess_sequel.engine.powers.kingPower;

/**
 * Marker interface for all king powers. Provides name, optional icon, and optional
 * description for display in the UI. Implemented by {@link ActiveKingPower},
 * {@link PassiveKingPower}, and {@link PreKingPower}.
 */
public interface KingPower {
    String getName();
    default String getIconPath() { return null; }
    default String getDescription() { return null; }
}
