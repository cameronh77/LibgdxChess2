package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.player.Player;

public interface ShopEffect {
    void apply(Player player);
    String getIconPath();
    String getName();
    default String getDescription() { return ""; }
    default boolean canPurchase(Player player) { return true; }
}
