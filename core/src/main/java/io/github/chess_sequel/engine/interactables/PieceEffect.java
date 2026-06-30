package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;

/** {@link ShopEffect} that adds a piece to the player's inventory for later placement during team setup. */
public class PieceEffect implements ShopEffect {

    private final Piece piece;

    public PieceEffect(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void apply(Player player) {
        player.getPieceInventory().add(piece);
    }

    @Override
    public String getIconPath() { return piece.getFilePath(); }

    @Override
    public String getName() { return piece.getName(); }
}
