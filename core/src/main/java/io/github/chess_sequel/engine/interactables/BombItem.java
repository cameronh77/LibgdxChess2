package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

/**
 * 1-use map item: enters bomb-targeting mode. The player then clicks an orthogonally
 * adjacent boulder to destroy it. The bomb is consumed whether or not a valid target
 * is clicked.
 */
public class BombItem extends ConsumableItem {

    @Override public String getName() { return "Bomb"; }
    @Override public String getIconPath() { return "items/bomb.png"; }
    @Override public String getDescription() { return "Destroy an orthogonally adjacent boulder."; }

    @Override
    public void onActivate(GameRun game) {
        game.setPendingBomb(this);
    }

    @Override
    public boolean isActive(GameRun game) {
        return game.getPendingBomb() == this;
    }

    @Override
    public void onDeactivate(GameRun game) {
        if (game.getPendingBomb() == this) game.cancelBomb();
    }
}
