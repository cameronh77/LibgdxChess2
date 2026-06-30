package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.location.board.ShopBoard;
import io.github.chess_sequel.engine.player.Player;

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
            ShopBoard board = (ShopBoard) game.getCurrentBoard();
            board.getTiles().get(col).get(row).setInteractable(null);
            board.getWares().remove(this);
            player.decrementCurrency(price);
            board.getShop().getWares().remove(this);
            game.setGameState(GameState.BOARD_STATE_CHANGED);
        }
    }

    public int getPrice() { return price; }
    public ShopEffect getEffect() { return effect; }
}
