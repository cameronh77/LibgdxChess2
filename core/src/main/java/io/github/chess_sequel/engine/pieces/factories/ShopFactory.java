package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.PieceEffect;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.jsonTypes.IndividualWare;
import io.github.chess_sequel.engine.pieces.Piece;

public class ShopFactory {

    public static ShopItem createShopItem(IndividualWare ware, GameRun game) {
        int price = Integer.valueOf(ware.price);
        ShopEffect effect;
        if (ware.ware.startsWith("power-")) {
            effect = KingPowerFactory.createEffect(ware.ware);
        } else {
            Piece piece = PieceFactory.generatePiece(ware.ware, ware.location.x, ware.location.y, true);
            effect = new PieceEffect(piece);
        }
        return new ShopItem(ware.location.x, ware.location.y, price, effect, game);
    }
}
