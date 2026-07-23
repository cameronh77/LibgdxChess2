package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.function.Function;

/**
 * {@link ShopEffect} that grants the player's king a new {@link io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower}.
 * Uses a factory function so the power can be constructed with a reference to the king instance.
 */
public class PassivePowerEffect implements ShopEffect {

    private final Function<Piece, PassiveKingPower> factory;
    private final String name;
    private final String iconPath;
    private final String description;

    public PassivePowerEffect(Function<Piece, PassiveKingPower> factory, String name, String iconPath, String description) {
        this.factory = factory;
        this.name = name;
        this.iconPath = iconPath;
        this.description = description;
    }

    @Override
    public void apply(Player player) {
        King k = player.getKing();
        if (k != null) k.addPassivePower(factory.apply(k));
    }

    @Override
    public boolean canPurchase(Player player) { return player.getKing() != null; }

    @Override
    public String getName() { return name; }

    @Override
    public String getIconPath() { return iconPath; }

    @Override
    public String getDescription() { return description; }
}
