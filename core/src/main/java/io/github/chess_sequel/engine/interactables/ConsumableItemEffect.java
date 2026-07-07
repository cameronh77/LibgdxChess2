package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.player.Player;

/**
 * {@link ShopEffect} wrapper that adds a {@link ConsumableItem} to the player's
 * consumable bag on purchase.
 */
public class ConsumableItemEffect implements ShopEffect {

    private final ConsumableItem item;

    public ConsumableItemEffect(ConsumableItem item) {
        this.item = item;
    }

    @Override
    public void apply(Player player) {
        player.getConsumables().add(item);
    }

    @Override public String getIconPath()    { return item.getIconPath(); }
    @Override public String getName()        { return item.getName(); }
    @Override public String getDescription() { return item.getDescription(); }
}
