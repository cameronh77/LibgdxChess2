package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.interactables.ActivePowerEffect;
import io.github.chess_sequel.engine.interactables.PassivePowerEffect;
import io.github.chess_sequel.engine.interactables.PreGamePowerEffect;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.powers.kingPower.BouncingBishopsPassive;
import io.github.chess_sequel.engine.powers.kingPower.MeekInheritPower;
import io.github.chess_sequel.engine.powers.kingPower.WinBonus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Maps {@code "power-*"} string identifiers (from JSON) to the corresponding {@link ShopEffect}
 * wrappers that grant king powers when purchased. Also provides orb offer generation.
 */
public class KingPowerFactory {

    /** Determines which power category pool an orb draws from. */
    public enum OrbType { ACTIVE, PASSIVE, MIXED }

    private static final List<String> ACTIVE_POOL  = Arrays.asList("power-meek-inherit");
    /** Passives and pre-game powers are offered together as the "passive" category. */
    private static final List<String> PASSIVE_POOL = Arrays.asList("power-bouncing-bishops", "power-win-bonus");

    /**
     * Randomly selects 2 power offers from the appropriate pool(s) based on orb type.
     * ACTIVE draws 2 actives, PASSIVE draws 2 passives/pre-games, MIXED draws 1 of each.
     */
    public static List<ShopEffect> generateOrbOffers(OrbType type) {
        List<String> active  = new ArrayList<>(ACTIVE_POOL);
        List<String> passive = new ArrayList<>(PASSIVE_POOL);
        Collections.shuffle(active);
        Collections.shuffle(passive);

        List<ShopEffect> offers = new ArrayList<>();
        switch (type) {
            case ACTIVE:
                for (int i = 0; i < Math.min(2, active.size()); i++) addIfNonNull(offers, active.get(i));
                break;
            case PASSIVE:
                for (int i = 0; i < Math.min(2, passive.size()); i++) addIfNonNull(offers, passive.get(i));
                break;
            case MIXED:
                if (!active.isEmpty())  addIfNonNull(offers, active.get(0));
                if (!passive.isEmpty()) addIfNonNull(offers, passive.get(0));
                break;
        }
        return offers;
    }

    private static void addIfNonNull(List<ShopEffect> list, String id) {
        ShopEffect e = createEffect(id);
        if (e != null) list.add(e);
    }

    /** Returns the {@link ShopEffect} for the given power identifier, or {@code null} if unrecognised. */
    public static ShopEffect createEffect(String name) {
        switch (name) {
            case "power-meek-inherit":
                return new ActivePowerEffect(
                    MeekInheritPower::new,
                    "The Meek Shall Inherit",
                    "kingPowers/meek-inherit.png",
                    "If all your pieces are pawns, swap one with your king."
                );
            case "power-bouncing-bishops":
                return new PassivePowerEffect(
                    BouncingBishopsPassive::new,
                    "Bouncing Bishops",
                    "kingPowers/bouncing-bishops.png",
                    "Bishops reflect off board edges to continue their diagonal."
                );
            case "power-win-bonus":
                return new PreGamePowerEffect(
                    k -> new WinBonus(),
                    "Victory Bonus",
                    "kingPowers/win-bonus.png",
                    "+5 Gold on victory."
                );
            default:
                return null;
        }
    }
}
