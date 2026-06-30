package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;

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
