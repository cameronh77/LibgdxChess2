package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;

import java.util.function.Function;

public class PreGamePowerEffect implements ShopEffect {

    private final Function<King, PreKingPower> factory;
    private final String name;
    private final String iconPath;
    private final String description;

    public PreGamePowerEffect(Function<King, PreKingPower> factory, String name, String iconPath, String description) {
        this.factory = factory;
        this.name = name;
        this.iconPath = iconPath;
        this.description = description;
    }

    @Override
    public void apply(Player player) {
        King k = player.getKing();
        if (k != null) k.addPreGamePower(factory.apply(k));
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
