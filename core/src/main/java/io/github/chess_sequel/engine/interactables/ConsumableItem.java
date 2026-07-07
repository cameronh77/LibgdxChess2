package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

/**
 * A single-use item held in the player's consumable bag. Activated from the map HUD.
 * Subclasses define what happens on activation (targeting mode, immediate effect, etc.).
 */
public abstract class ConsumableItem {
    public abstract String getName();
    public abstract String getIconPath();
    public abstract String getDescription();
    public abstract void onActivate(GameRun game);
    /** Returns true when this item is currently the active/selected item. */
    public abstract boolean isActive(GameRun game);
    /** Cancels the active state without consuming the item. */
    public abstract void onDeactivate(GameRun game);
}
