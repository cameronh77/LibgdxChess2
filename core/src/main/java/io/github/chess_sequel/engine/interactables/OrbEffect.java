package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.pieces.factories.KingPowerFactory;
import io.github.chess_sequel.engine.player.Player;

import java.util.List;

/**
 * A shop effect that presents the player with a random choice of king powers.
 * On purchase it generates two offers from the appropriate pool and sets them
 * as the {@link GameRun#setPendingPowerOffer pending power offer}, which the UI
 * then surfaces as a pick-one selection panel.
 *
 * Normal orbs (MIXED/RANDOM) draw from the player's class pool.
 * MAP orbs draw from the current zone's pool.
 */
public class OrbEffect implements ShopEffect {

    private final KingPowerFactory.OrbType orbType;
    private final GameRun gameRun;

    public OrbEffect(KingPowerFactory.OrbType orbType, GameRun gameRun) {
        this.orbType = orbType;
        this.gameRun = gameRun;
    }

    @Override
    public void apply(Player player) {
        List<ShopEffect> offers = KingPowerFactory.generateOrbOffers(
            orbType,
            player.getKing(),
            player.getPlayerClass(),
            gameRun.getCurrentMap()
        );
        gameRun.setPendingPowerOffer(offers);
    }

    @Override
    public boolean canPurchase(Player player) { return player.getKing() != null; }

    @Override
    public String getName() {
        switch (orbType) {
            case MAP:     return "Orb of the Land";
            default:      return "Orb of Power";
        }
    }

    @Override
    public String getIconPath() {
        switch (orbType) {
            case MAP:     return "orbs/orb-map.png";
            default:      return "orbs/orb-mixed.png";
        }
    }

    @Override
    public String getDescription() {
        switch (orbType) {
            case MAP:     return "Choose one of 2 powers drawn from this region.";
            default:      return "Choose one of 2 powers from your class.";
        }
    }
}
