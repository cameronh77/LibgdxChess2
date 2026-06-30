package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;

public class PieceFactory {

    public static Piece generatePiece(String pieceName, int x, int y, boolean isShop) {
        switch (pieceName) {
            case "classic-pawn":   return ClassicFactory.createPiece('p', x, y, isShop);
            case "classic-bishop": return ClassicFactory.createPiece('b', x, y, isShop);
            case "classic-horse":  return ClassicFactory.createPiece('h', x, y, isShop);
            case "classic-queen":  return ClassicFactory.createPiece('q', x, y, isShop);
            case "classic-king":   return ClassicFactory.createPiece('k', x, y, isShop);
            case "classic-castle": return ClassicFactory.createPiece('c', x, y, isShop);
            case "goblin-pawn":    return GoblinFactory.createPiece('p', x, y, isShop);
            case "goblin-drill":   return GoblinFactory.createPiece('b', x, y, isShop);
            case "slime-steed":    return GoblinFactory.createPiece('h', x, y, isShop);
            case "goblin-queen":   return GoblinFactory.createPiece('q', x, y, isShop);
            case "goblin-king":    return GoblinFactory.createPiece('k', x, y, isShop);
            case "toll-gate":      return GoblinFactory.createPiece('c', x, y, isShop);
            default:               return null;
        }
    }
}
