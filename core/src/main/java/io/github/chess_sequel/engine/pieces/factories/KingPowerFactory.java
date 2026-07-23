package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.interactables.ActivePowerEffect;
import io.github.chess_sequel.engine.interactables.PassivePowerEffect;
import io.github.chess_sequel.engine.interactables.PreGamePowerEffect;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.classic.AllPassantPassive;
import io.github.chess_sequel.engine.powers.kingPower.classic.BouncingBishopsPassive;
import io.github.chess_sequel.engine.powers.kingPower.classic.MeekInheritPower;
import io.github.chess_sequel.engine.powers.kingPower.classic.PanopticonPassive;
import io.github.chess_sequel.engine.powers.kingPower.classic.QueenlyMajestyPassive;
import io.github.chess_sequel.engine.powers.kingPower.classic.TwentyFourHourEnergyPassive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.BloodFrenzyPassive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.CreateLifeActive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.FortifyPassive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.GoblinSwapActive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.SlimeShieldPassive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.StickySlimePassive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.ToxicCatapultActive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.ToxicSpillActive;
import io.github.chess_sequel.engine.powers.kingPower.goblin.WinBonus;
import io.github.chess_sequel.engine.powers.kingPower.strategy.DeadfallPrePower;
import io.github.chess_sequel.engine.powers.kingPower.strategy.HydraTrapsPassive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.OverwatchPassive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.PersistencePassive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.ForcedVariationPassive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.FullCommandPassive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.KingSnareActive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.ProphylaxisPassive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.ZugzwangActive;
import io.github.chess_sequel.engine.powers.kingPower.strategy.StrongholdPassive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Maps {@code "power-*"} string identifiers (from JSON) to the corresponding {@link ShopEffect}
 * wrappers that grant king powers when purchased. Also provides orb offer generation.
 *
 * Powers are tagged with a class affinity (e.g. "classic", "goblin") so that the normal orb
 * only offers powers belonging to the player's class. A separate map orb draws from a
 * zone-specific pool registered via {@link #registerZonePower}.
 */
public class KingPowerFactory {

    /** MIXED/RANDOM = class orb (draws from the player's class pool). MAP = zone-specific orb. */
    public enum OrbType { ACTIVE, PASSIVE, MIXED, RANDOM, MAP }

    private static final Random RNG = new Random();

    /** All known powers, ordered: actives first then passives. */
    private static final List<String> ALL_POWERS = Arrays.asList(
        "power-meek-inherit",
        "power-bouncing-bishops", "power-win-bonus",
        "power-queenly-majesty", "power-panopticon",
        "power-24hr-energy", "power-all-passant",
        "power-goblin-swap",
        "power-toxic-spill",
        "power-toxic-catapult",
        "power-slime-shield",
        "power-create-life",
        "power-stickier-slime",
        "power-fortify",
        "power-blood-frenzy",
        "power-deadfall",
        "power-overwatch",
        "power-persistence",
        "power-hydra-traps",
        "power-stronghold",
        "power-forced-variation",
        "power-full-command",
        "power-kings-snare",
        "power-prophylaxis",
        "power-zugzwang"
    );

    /** Which player class each power belongs to. */
    private static final Map<String, String> POWER_CLASS = new HashMap<String, String>() {{
        put("power-meek-inherit",           "classic");
        put("power-bouncing-bishops",       "classic");
        put("power-win-bonus",              "goblin");
        put("power-queenly-majesty",        "classic");
        put("power-panopticon",             "classic");
        put("power-24hr-energy",            "classic");
        put("power-all-passant",            "classic");
        put("power-goblin-swap",            "goblin");
        put("power-toxic-spill",            "goblin");
        put("power-toxic-catapult",         "goblin");
        put("power-slime-shield",           "goblin");
        put("power-create-life",            "goblin");
        put("power-stickier-slime",         "goblin");
        put("power-fortify",                "goblin");
        put("power-blood-frenzy",           "goblin");
        put("power-deadfall",               "strategy");
        put("power-overwatch",              "strategy");
        put("power-persistence",            "strategy");
        put("power-hydra-traps",            "strategy");
        put("power-stronghold",             "strategy");
        put("power-forced-variation",       "strategy");
        put("power-full-command",           "strategy");
        put("power-kings-snare",            "strategy");
        put("power-prophylaxis",            "strategy");
        put("power-zugzwang",               "strategy");
    }};

    private static final Map<String, List<String>> PREREQUISITES = new HashMap<String, List<String>>() {{
        put("power-persistence",  Arrays.asList("power-deadfall"));
        put("power-overwatch",    Arrays.asList("power-deadfall"));
        put("power-hydra-traps",  Arrays.asList("power-persistence"));
        put("power-prophylaxis",  Arrays.asList("power-stronghold"));
    }};

    /** Zone-specific power pools. Populated at runtime by zone setup code. */
    private static final Map<String, List<String>> ZONE_POOLS = new HashMap<>();

    /** Registers a power as available in a specific zone's map orb pool. */
    public static void registerZonePower(String zoneId, String powerId) {
        ZONE_POOLS.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(powerId);
    }

    /**
     * Generates 2 power offers for an orb purchase.
     *
     * For MIXED/RANDOM/ACTIVE/PASSIVE orbs: draws from the player's class pool.
     * For MAP orbs: draws from the current zone's pool.
     * Already-owned powers are always excluded.
     */
    public static List<ShopEffect> generateOrbOffers(OrbType type, King king, String playerClass, String zoneId) {
        Set<String> owned = getOwnedIds(king);

        if (type == OrbType.MAP) {
            List<String> pool = new ArrayList<>(ZONE_POOLS.getOrDefault(zoneId, Collections.emptyList()));
            pool.removeIf(owned::contains);
            pool.removeIf(id -> !prerequisitesMet(id, owned));
            Collections.shuffle(pool);
            List<ShopEffect> offers = new ArrayList<>();
            for (int i = 0; i < Math.min(2, pool.size()); i++) addIfNonNull(offers, pool.get(i));
            return offers;
        }

        // Class orb: filter ALL_POWERS to those matching the player's class
        List<String> pool = new ArrayList<>();
        for (String id : ALL_POWERS) {
            if (playerClass.equals(POWER_CLASS.get(id))) pool.add(id);
        }
        pool.removeIf(owned::contains);
        pool.removeIf(id -> !prerequisitesMet(id, owned));
        Collections.shuffle(pool);

        List<ShopEffect> offers = new ArrayList<>();
        for (int i = 0; i < Math.min(2, pool.size()); i++) addIfNonNull(offers, pool.get(i));
        return offers;
    }

    private static boolean prerequisitesMet(String id, Set<String> owned) {
        List<String> prereqs = PREREQUISITES.get(id);
        return prereqs == null || owned.containsAll(prereqs);
    }

    private static Set<String> getOwnedIds(Piece piece) {
        Set<String> ids = new HashSet<>();
        if (piece == null) return ids;
        for (ActiveKingPower p  : piece.getActivePowers())  { String id = powerToId(p); if (id != null) ids.add(id); }
        for (PassiveKingPower p : piece.getPassivePowers())  { String id = powerToId(p); if (id != null) ids.add(id); }
        for (PreKingPower p     : piece.getPreGamePowers())  { String id = powerToId(p); if (id != null) ids.add(id); }
        return ids;
    }

    /** Directly attaches a named power to any piece — used when assigning powers to bot lead pieces. */
    public static void applyPowerToPiece(String id, Piece piece) {
        switch (id) {
            case "power-bouncing-bishops":  piece.addPassivePower(new BouncingBishopsPassive(piece)); break;
            case "power-queenly-majesty":   piece.addPassivePower(new QueenlyMajestyPassive(piece));  break;
            case "power-panopticon":        piece.addPassivePower(new PanopticonPassive(piece));       break;
            case "power-24hr-energy":       piece.addPassivePower(new TwentyFourHourEnergyPassive(piece)); break;
            case "power-all-passant":       piece.addPassivePower(new AllPassantPassive(piece));       break;
            case "power-slime-shield":      piece.addPassivePower(new SlimeShieldPassive(piece));      break;
            case "power-stickier-slime":    piece.addPassivePower(new StickySlimePassive(piece));      break;
            case "power-fortify":           piece.addPassivePower(new FortifyPassive(piece));          break;
            case "power-blood-frenzy":      piece.addPassivePower(new BloodFrenzyPassive(piece));      break;
            case "power-deadfall":          piece.addPreGamePower(new DeadfallPrePower(piece));        break;
            case "power-overwatch":         piece.addPassivePower(new OverwatchPassive(piece));        break;
            case "power-persistence":       piece.addPassivePower(new PersistencePassive(piece));      break;
            case "power-hydra-traps":       piece.addPassivePower(new HydraTrapsPassive(piece));       break;
            case "power-stronghold":        piece.addPassivePower(new StrongholdPassive(piece));       break;
            case "power-forced-variation":  piece.addPassivePower(new ForcedVariationPassive(piece));  break;
            case "power-full-command":      piece.addPassivePower(new FullCommandPassive(piece));      break;
            case "power-kings-snare":       piece.addActivePower(new KingSnareActive(piece));          break;
            case "power-prophylaxis":       piece.addPassivePower(new ProphylaxisPassive(piece));      break;
            case "power-zugzwang":          piece.addActivePower(new ZugzwangActive(piece));            break;
        }
    }

    private static String powerToId(Object power) {
        if (power instanceof MeekInheritPower)              return "power-meek-inherit";
        if (power instanceof BouncingBishopsPassive)        return "power-bouncing-bishops";
        if (power instanceof WinBonus)                      return "power-win-bonus";
        if (power instanceof QueenlyMajestyPassive)         return "power-queenly-majesty";
        if (power instanceof PanopticonPassive)             return "power-panopticon";
        if (power instanceof TwentyFourHourEnergyPassive)   return "power-24hr-energy";
        if (power instanceof AllPassantPassive)             return "power-all-passant";
        if (power instanceof GoblinSwapActive)              return "power-goblin-swap";
        if (power instanceof ToxicSpillActive)              return "power-toxic-spill";
        if (power instanceof ToxicCatapultActive)           return "power-toxic-catapult";
        if (power instanceof SlimeShieldPassive)            return "power-slime-shield";
        if (power instanceof CreateLifeActive)              return "power-create-life";
        if (power instanceof StickySlimePassive)            return "power-stickier-slime";
        if (power instanceof FortifyPassive)                return "power-fortify";
        if (power instanceof BloodFrenzyPassive)            return "power-blood-frenzy";
        if (power instanceof DeadfallPrePower)              return "power-deadfall";
        if (power instanceof OverwatchPassive)              return "power-overwatch";
        if (power instanceof PersistencePassive)            return "power-persistence";
        if (power instanceof HydraTrapsPassive)             return "power-hydra-traps";
        if (power instanceof StrongholdPassive)             return "power-stronghold";
        if (power instanceof ForcedVariationPassive)        return "power-forced-variation";
        if (power instanceof FullCommandPassive)            return "power-full-command";
        if (power instanceof KingSnareActive)               return "power-kings-snare";
        if (power instanceof ProphylaxisPassive)            return "power-prophylaxis";
        if (power instanceof ZugzwangActive)                return "power-zugzwang";
        return null;
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
            case "power-queenly-majesty":
                return new PassivePowerEffect(
                    QueenlyMajestyPassive::new,
                    "Queenly Majesty",
                    "kingPowers/queenly-majesty.png",
                    "Pieces adjacent to your Queen can move like a Queen."
                );
            case "power-panopticon":
                return new PassivePowerEffect(
                    PanopticonPassive::new,
                    "Panopticon",
                    "kingPowers/panopticon.png",
                    "Your Pawns can move one square in any orthogonal direction (non-capturing)."
                );
            case "power-24hr-energy":
                return new PassivePowerEffect(
                    TwentyFourHourEnergyPassive::new,
                    "24 Hour Energy",
                    "kingPowers/24hr-energy.png",
                    "Your Pawns can always advance 2 squares forward, not just on their first move."
                );
            case "power-all-passant":
                return new PassivePowerEffect(
                    AllPassantPassive::new,
                    "All Passant",
                    "kingPowers/all-passant.png",
                    "Your Pawns can en passant any adjacent enemy Pawn, regardless of when it moved."
                );
            case "power-goblin-swap":
                return new ActivePowerEffect(
                    GoblinSwapActive::new,
                    "Goblin's Gambit",
                    "kingPowers/goblin-surge.png",
                    "Once per game, when you've lost 50%+ of your pieces, swap your King with any piece on the board."
                );
            case "power-toxic-spill":
                return new ActivePowerEffect(
                    ToxicSpillActive::new,
                    "Toxic Spill",
                    "kingPowers/toxic-spill.png",
                    "Once per game, scatter slime across the board — one tile per surviving friendly piece."
                );
            case "power-toxic-catapult":
                return new ActivePowerEffect(
                    ToxicCatapultActive::new,
                    "Toxic Catapult",
                    "kingPowers/toxic-catapult.png",
                    "Once per game, surround any piece with slime."
                );
            case "power-slime-shield":
                return new PassivePowerEffect(
                    SlimeShieldPassive::new,
                    "Slime Shield",
                    "kingPowers/slime-shield.png",
                    "When your King moves, slime appears on adjacent cardinal tiles."
                );
            case "power-create-life":
                return new ActivePowerEffect(
                    CreateLifeActive::new,
                    "Create Life",
                    "kingPowers/create-life.png",
                    "Consume all slime to summon a goblin piece — the more slime, the stronger the piece."
                );
            case "power-stickier-slime":
                return new PassivePowerEffect(
                    StickySlimePassive::new,
                    "Stickier Slime",
                    "kingPowers/stickier-slime.png",
                    "Your slime stuns enemies for 2 turns instead of 1."
                );
            case "power-fortify":
                return new PassivePowerEffect(
                    FortifyPassive::new,
                    "Fortify",
                    "kingPowers/fortify.png",
                    "Friendly pieces on a TollGate's row cannot be captured. Only the TollGate itself remains vulnerable."
                );
            case "power-blood-frenzy":
                return new PassivePowerEffect(
                    BloodFrenzyPassive::new,
                    "Blood Frenzy",
                    "kingPowers/blood-frenzy.png",
                    "After any friendly capture, all other friendly pieces may each make one move. Capturing continues the chain; any other move ends it."
                );
            case "power-deadfall":
                return new PreGamePowerEffect(
                    DeadfallPrePower::new,
                    "Deadfall",
                    "kingPowers/deadfall.png",
                    "Each TrapPawn begins the match with a trap already set."
                );
            case "power-overwatch":
                return new PassivePowerEffect(
                    OverwatchPassive::new,
                    "Overwatch",
                    "kingPowers/overwatch.png",
                    "TrapPawns can place traps 2 squares diagonally forward if both squares are clear."
                );
            case "power-persistence":
                return new PassivePowerEffect(
                    PersistencePassive::new,
                    "Persistence",
                    "kingPowers/persistence.png",
                    "TrapPawns keep their old trap when placing a new one, accumulating traps over time."
                );
            case "power-hydra-traps":
                return new PassivePowerEffect(
                    HydraTrapsPassive::new,
                    "Hydra Traps",
                    "kingPowers/hydra-traps.png",
                    "When a friendly trap is triggered, a new trap spawns at a random empty tile, owned by the same pawn."
                );
            case "power-stronghold":
                return new PassivePowerEffect(
                    StrongholdPassive::new,
                    "Stronghold",
                    "kingPowers/stronghold.png",
                    "Ramparts project a full cross barrier — enemies cannot cross the column a Rampart occupies, in addition to its row."
                );
            case "power-forced-variation":
                return new PassivePowerEffect(
                    ForcedVariationPassive::new,
                    "Forced Variation",
                    "kingPowers/forced-variation.png",
                    "Neither side can move the same piece twice in a row, unless it has no other legal moves."
                );
            case "power-full-command":
                return new PassivePowerEffect(
                    FullCommandPassive::new,
                    "Full Command",
                    "kingPowers/full-command.png",
                    "The Commander's free-move aura extends to all adjacent friendly pieces, not just pawns."
                );
            case "power-kings-snare":
                return new ActivePowerEffect(
                    KingSnareActive::new,
                    "King's Snare",
                    "kingPowers/kings-snare.png",
                    "Once per game, place a trap on any empty tile. When triggered, the king teleports there — a powerful capture that may leave the king dangerously exposed."
                );
            case "power-prophylaxis":
                return new PassivePowerEffect(
                    ProphylaxisPassive::new,
                    "Prophylaxis",
                    "kingPowers/prophylaxis.png",
                    "Squares covered by 2 or more friendly pieces are locked — enemy sliding pieces cannot move through them, only to them."
                );
            case "power-zugzwang":
                return new ActivePowerEffect(
                    ZugzwangActive::new,
                    "Zugzwang",
                    "kingPowers/zugzwang.png",
                    "Once per game, force an enemy piece to move next turn — all other enemy pieces are frozen. If the chosen piece has no legal moves, the opponent loses their turn entirely."
                );
            default:
                return null;
        }
    }
}
