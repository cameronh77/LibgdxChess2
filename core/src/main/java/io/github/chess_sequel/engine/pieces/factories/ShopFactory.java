package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.OrbEffect;
import io.github.chess_sequel.engine.interactables.PieceEffect;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.jsonTypes.IndividualWare;
import io.github.chess_sequel.engine.jsonTypes.MapNode;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Creates {@link io.github.chess_sequel.engine.interactables.ShopItem} instances from JSON node
 * or ware data. Dispatches to {@link KingPowerFactory} for {@code "power-*"} refs and to
 * {@link PieceFactory} for piece refs.
 */
public class ShopFactory {

    public static ShopItem createShopItem(MapNode node, GameRun game) {
        ShopEffect effect;
        if (node.ref.startsWith("power-")) {
            effect = KingPowerFactory.createEffect(node.ref);
        } else if (node.ref.startsWith("orb-")) {
            effect = new OrbEffect(orbTypeFromRef(node.ref), game);
        } else {
            Piece piece = PieceFactory.generatePiece(node.ref, node.x, node.y, true);
            effect = new PieceEffect(piece);
        }
        return new ShopItem(node.x, node.y, node.price, effect, game);
    }

    public static ShopItem createShopItem(IndividualWare ware, GameRun game) {
        int price = Integer.valueOf(ware.price);
        ShopEffect effect;
        if (ware.ware.startsWith("power-")) {
            effect = KingPowerFactory.createEffect(ware.ware);
        } else if (ware.ware.startsWith("orb-")) {
            effect = new OrbEffect(orbTypeFromRef(ware.ware), game);
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
            default:            return KingPowerFactory.OrbType.MIXED;
        }
    }
}
