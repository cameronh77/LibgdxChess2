package io.github.chess_sequel.engine.roster;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.*;
import io.github.chess_sequel.engine.pieces.goblin.*;
import io.github.chess_sequel.engine.pieces.war.conflict.*;
import io.github.chess_sequel.engine.pieces.war.loss.*;
import io.github.chess_sequel.engine.pieces.war.strategy.*;
import io.github.chess_sequel.engine.powers.kingPower.classic.BouncingBishopsPassive;
import io.github.chess_sequel.engine.powers.kingPower.classic.MeekInheritPower;
import io.github.chess_sequel.engine.powers.kingPower.goblin.WinBonus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static registry of all playable king archetypes and their starting team presets.
 * Each {@link KingDef} maps a king archetype to a list of {@link TeamPreset}s. The
 * KingSelectionScreen reads this list to build the character-select UI.
 */
public class KingRoster {

    public static final List<KingDef> KINGS = new ArrayList<>();

    static {
        KINGS.add(new KingDef(
            "Classic King",
            "pieces/classic/white-king.png",
            Arrays.asList(
                new TeamPreset("The Meek", () -> {
                    King k = classicKing(0, 0);
                    return mutable(k, new Pawn(1, 0, false), new Pawn(2, 0, false), new Pawn(3, 0, false));
                }),
                new TeamPreset("The Church", () -> {
                    King k = classicKing(0, 0);
                    return mutable(k, new Bishop(1, 0, false), new Bishop(2, 0, false));
                }),
                new TeamPreset("The Monarchy", () -> {
                    King k = classicKing(0, 0);
                    return mutable(k, new Queen(1, 0, false));
                })
            )
        ));

        KINGS.add(new KingDef(
            "Goblin King",
            "pieces/goblin/white-goblin-king.png",
            Arrays.asList(
                new TeamPreset("Horde", () -> {
                    GoblinKing k = new GoblinKing(0, 0, false);
                    return mutable(k, new Goblin(1, 0, false), new Goblin(2, 0, false), new Goblin(3, 0, false));
                }),
                new TeamPreset("War Party", () -> {
                    GoblinKing k = new GoblinKing(0, 0, false);
                    return mutable(k, new TollGate(1, 0, false), new SlimeSteed(2, 0, false), new GoblinQueen(3, 0, false));
                }),
                new TeamPreset("Drill Squad", () -> {
                    GoblinKing k = new GoblinKing(0, 0, false);
                    return mutable(k, new GoblinDrill(1, 0, false), new GoblinDrill(2, 0, false), new GoblinDrill(3, 0, false));
                })
            )
        ));

        KINGS.add(new KingDef(
            "Strategy King",
            "pieces/goblin/white-strategy-king.png",
            Arrays.asList(
                new TeamPreset("Trappers", () -> {
                    StrategyKing k = new StrategyKing(0, 0, false);
                    return mutable(k, new TrapPawn(1, 0, false), new TrapPawn(2, 0, false), new TrapPawn(3, 0, false));
                }),
                new TeamPreset("Far Seers", () -> {
                    StrategyKing k = new StrategyKing(0, 0, false);
                    return mutable(k, new ProphetBishop(1, 0, false), new ProphetBishop(2, 0, false));
                    }),
                new TeamPreset("Lead the charge", () -> {
                    StrategyKing k = new StrategyKing(0, 0, false);
                    return mutable(k, new TrapPawn(1, 0, false), new Commander(2, 0, false));
                }),
                new TeamPreset("Royal Planning", () -> {
                    StrategyKing k = new StrategyKing(0, 0, false);
                    return mutable(k, new StrategyQueen(1, 0, false));
                })
            )
        ));

        KINGS.add(new KingDef(
            "Conflict King",
            "pieces/conflict/white-conflict-king.png",
            Arrays.asList(
                new TeamPreset("Raiding Party", () -> {
                    ConflictKing k = new ConflictKing(0, 0, false);
                    return mutable(k, new Barbarian(1, 0, false), new Barbarian(2, 0, false), new Barbarian(3, 0, false));
                }),
                new TeamPreset("Siege", () -> {
                    ConflictKing k = new ConflictKing(0, 0, false);
                    return mutable(k, new Trebuchet(1, 0, false), new Trebuchet(2, 0, false));
                }),
                new TeamPreset("Cavalry Charge", () -> {
                    ConflictKing k = new ConflictKing(0, 0, false);
                    return mutable(k, new Cavalry(1, 0, false), new Cavalry(2, 0, false), new ConflictBishop(3, 0, false));
                }),
                new TeamPreset("Berserker", () -> {
                    ConflictKing k = new ConflictKing(0, 0, false);
                    return mutable(k, new Berserker(1, 0, false), new Barbarian(2, 0, false));
                })
            )
        ));

        KINGS.add(new KingDef(
            "Loss King",
            "pieces/loss/white-loss-king.png",
            Arrays.asList(
                new TeamPreset("Haunted", () -> {
                    LossKing k = new LossKing(0, 0, false);
                    return mutable(k, new HorselessHeadman(1, 0, false), new LossPawn(2, 0, false), new LossPawn(3, 0, false));
                }),
                new TeamPreset("Phylactery", () -> {
                    LossKing k = new LossKing(0, 0, false);
                    return mutable(k, new PhylacteryQueen(1, 0, false), new LossBishop(2, 0, false));
                }),
                new TeamPreset("Ruins", () -> {
                    LossKing k = new LossKing(0, 0, false);
                    return mutable(k, new LossCastle(1, 0, false), new LossCastle(2, 0, false));
                }),
                new TeamPreset("Sovereign", () -> {
                    LossKing k = new LossKing(0, 0, false);
                    return mutable(k, new PhylacteryQueen(1, 0, false), new HorselessHeadman(2, 0, false), new LossBishop(3, 0, false));
                })
            )
        ));
    }

    private static King classicKing(int col, int row) {
        King k = new King(col, row, false);
        k.addActivePower(new MeekInheritPower(k));
        k.addPassivePower(new BouncingBishopsPassive(k));
        k.addPreGamePower(new WinBonus());
        return k;
    }

    @SafeVarargs
    private static <T> ArrayList<T> mutable(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
