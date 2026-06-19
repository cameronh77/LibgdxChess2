package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.jsonTypes.IndividualWare;

public class ShopFactory {
    public static ShopItem createShopItem(IndividualWare ware, GameRun game){
        return new ShopItem(ware.location.x, ware.location.y, Integer.valueOf(ware.price), PieceFactory.generatePiece(String.valueOf(ware.ware) +""+ ware.location.x +""+ ware.location.y, true), game);
    }
}
