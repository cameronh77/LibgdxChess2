package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.BombItem;
import io.github.chess_sequel.engine.interactables.ConsumableItemEffect;
import io.github.chess_sequel.engine.interactables.EssenceItem;
import io.github.chess_sequel.engine.interactables.OrbEffect;
import io.github.chess_sequel.engine.interactables.PieceEffect;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.jsonTypes.IndividualWare;
import io.github.chess_sequel.engine.jsonTypes.MapNode;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.section.SectionLayout;

import java.util.Arrays;
import java.util.List;

/**
 * Creates {@link io.github.chess_sequel.engine.interactables.ShopItem} instances from JSON node
 * or ware data. Dispatches to {@link KingPowerFactory} for {@code "power-*"} refs and to
 * {@link PieceFactory} for piece refs.
 */
public class ShopFactory {

    private static final List<String> CLASSIC_PIECES  = Arrays.asList(
        "classic-pawn", "classic-bishop", "classic-horse", "classic-castle", "classic-queen");
    private static final List<String> GOBLIN_PIECES   = Arrays.asList(
        "goblin-pawn", "goblin-drill", "slime-steed", "toll-gate", "goblin-queen");
    private static final List<String> STRATEGY_PIECES = Arrays.asList(
        "trap-pawn", "prophet-bishop", "commander", "rampart", "strategy-queen");
    private static final List<String> CONFLICT_PIECES = Arrays.asList(
        "barbarian", "conflict-bishop", "cavalry", "berserker", "trebuchet");
    private static final List<String> LOSS_PIECES     = Arrays.asList(
        "loss-pawn", "loss-bishop", "horseless-headman", "loss-castle", "phylactery-queen");

    public static ShopItem createShopItem(MapNode node, GameRun game) {
        ShopEffect effect;
        if (node.ref.startsWith("power-")) {
            effect = KingPowerFactory.createEffect(node.ref);
        } else if (node.ref.startsWith("orb-")) {
            effect = new OrbEffect(orbTypeFromRef(node.ref), game);
        } else if (node.ref.startsWith("item-")) {
            effect = consumableEffectFromRef(node.ref);
        } else if (node.ref.equals("piece-faction")) {
            Piece piece = generateFactionPiece(node.x, node.y, game);
            effect = new PieceEffect(piece);
        } else {
            Piece piece = PieceFactory.generatePiece(node.ref, node.x, node.y, true);
            effect = new PieceEffect(piece);
        }
        return new ShopItem(node.x, node.y, node.price, effect, game);
    }

    private static Piece generateFactionPiece(int x, int y, GameRun game) {
        String playerFaction = game.getPlayer().getPlayerClass();
        SectionLayout section = game.getActiveSectionLayout();
        String sectionId = section != null ? section.sectionId : null;

        List<String> primaryPool;
        if ("classic".equals(playerFaction)) {
            // Classic King inverts the rule: 80% from the current section's faction
            primaryPool = getFactionPieces(sectionId != null ? sectionId : "classic");
        } else {
            // All other kings: 80% from own faction
            primaryPool = getFactionPieces(playerFaction);
        }

        String ref = Math.random() < 0.8
            ? primaryPool.get((int)(Math.random() * primaryPool.size()))
            : CLASSIC_PIECES.get((int)(Math.random() * CLASSIC_PIECES.size()));

        return PieceFactory.generatePiece(ref, x, y, true);
    }

    private static List<String> getFactionPieces(String faction) {
        switch (faction) {
            case "goblin":   return GOBLIN_PIECES;
            case "strategy": return STRATEGY_PIECES;
            case "conflict": return CONFLICT_PIECES;
            case "loss":     return LOSS_PIECES;
            default:         return CLASSIC_PIECES;
        }
    }

    public static ShopItem createShopItem(IndividualWare ware, GameRun game) {
        int price = Integer.valueOf(ware.price);
        ShopEffect effect;
        if (ware.ware.startsWith("power-")) {
            effect = KingPowerFactory.createEffect(ware.ware);
        } else if (ware.ware.startsWith("orb-")) {
            effect = new OrbEffect(orbTypeFromRef(ware.ware), game);
        } else if (ware.ware.startsWith("item-")) {
            effect = consumableEffectFromRef(ware.ware);
        } else {
            Piece piece = PieceFactory.generatePiece(ware.ware, ware.location.x, ware.location.y, true);
            effect = new PieceEffect(piece);
        }
        return new ShopItem(ware.location.x, ware.location.y, price, effect, game);
    }

    private static KingPowerFactory.OrbType orbTypeFromRef(String ref) {
        switch (ref) {
            case "orb-active":  return KingPowerFactory.OrbType.ACTIVE;
            case "orb-passive": return KingPowerFactory.OrbType.PASSIVE;
            case "orb-mixed":   return KingPowerFactory.OrbType.MIXED;
            case "orb-map":     return KingPowerFactory.OrbType.MAP;
            default:            return KingPowerFactory.OrbType.RANDOM;
        }
    }

    /** Maps "item-bomb", "item-essence-queen", etc. to a {@link ConsumableItemEffect}. */
    public static ConsumableItemEffect consumableEffectFromRef(String ref) {
        if (ref.equals("item-bomb")) {
            return new ConsumableItemEffect(new BombItem());
        }
        if (ref.startsWith("item-essence-")) {
            String pieceType = ref.substring("item-essence-".length());
            return new ConsumableItemEffect(new EssenceItem(pieceType));
        }
        return null;
    }
}
