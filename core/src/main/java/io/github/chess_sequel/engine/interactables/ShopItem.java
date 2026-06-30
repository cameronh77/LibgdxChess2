package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.player.Player;

/**
 * A purchasable item on the map. When the player steps on it and has enough currency,
 * the {@link ShopEffect} is applied, the item is removed from the board, and the gold cost
 * is deducted. Does nothing if the player can't afford it or the effect's canPurchase check fails.
 */
public class ShopItem extends Interactable {

    private final int price;
    private final ShopEffect effect;
    private final GameRun game;

    public ShopItem(int col, int row, int price, ShopEffect effect, GameRun game) {
        this.col = col;
        this.row = row;
        this.price = price;
        this.effect = effect;
        this.filePath = effect.getIconPath();
        this.game = game;
    }

    @Override
    public void interaction() {
        Player player = game.getPlayer();
        if (player.getCurrency() >= price && effect.canPurchase(player)) {
            effect.apply(player);
            MapBoard board = (MapBoard) game.getCurrentBoard();
            board.getTiles().get(col).get(row).setInteractable(null);
            board.getLocations().remove(this);
            player.decrementCurrency(price);
            game.setGameState(GameState.BOARD_STATE_CHANGED);
        }
    }

    public int getPrice() { return price; }
    public ShopEffect getEffect() { return effect; }
}
