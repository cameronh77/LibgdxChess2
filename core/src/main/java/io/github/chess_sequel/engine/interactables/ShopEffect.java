package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.player.Player;

/**
 * Strategy interface for what happens when a {@link ShopItem} is purchased.
 * Implementations cover pieces, active/passive/pre-game king powers, etc.
 */
public interface ShopEffect {
    /** Applies the effect to the player (add piece, grant power, etc.). */
    void apply(Player player);
    String getIconPath();
    String getName();
    default String getDescription() { return ""; }
    /** Returns {@code false} if this effect cannot be purchased in the current player state (e.g. no king). */
    default boolean canPurchase(Player player) { return true; }
}
