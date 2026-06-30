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
        List<ShopEffect> offers = KingPowerFactory.generateOrbOffers(orbType);
        gameRun.setPendingPowerOffer(offers);
    }

    @Override
    public boolean canPurchase(Player player) { return player.getKing() != null; }

    @Override
    public String getName() {
        switch (orbType) {
            case ACTIVE:  return "Orb of Power";
            case PASSIVE: return "Orb of Wisdom";
            default:      return "Orb of Chance";
        }
    }

    @Override
    public String getIconPath() {
        switch (orbType) {
            case ACTIVE:  return "orbs/orb-active.png";
            case PASSIVE: return "orbs/orb-passive.png";
            default:      return "orbs/orb-mixed.png";
        }
    }

    @Override
    public String getDescription() {
        switch (orbType) {
            case ACTIVE:  return "Choose one of 2 random active powers.";
            case PASSIVE: return "Choose one of 2 random passive or pre-game powers.";
            default:      return "Choose between a random active and a random passive power.";
        }
    }
}
