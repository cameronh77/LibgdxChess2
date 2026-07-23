package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;

/**
 * Central factory that maps string identifiers (from JSON) to concrete piece instances.
 * Delegates to {@link ClassicFactory} or {@link GoblinFactory} based on the faction prefix.
 */
public class PieceFactory {

    /**
     * Creates a piece for the given name string, position, and context.
     * @param isShop {@code true} when the piece is being created for a shop display rather than active play
     */
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
            case "trap-pawn":      return StrategyFactory.createPiece('p', x, y, isShop);
            case "prophet-bishop": return StrategyFactory.createPiece('b', x, y, isShop);
            case "commander":      return StrategyFactory.createPiece('h', x, y, isShop);
            case "rampart":        return StrategyFactory.createPiece('c', x, y, isShop);
            case "strategy-queen": return StrategyFactory.createPiece('q', x, y, isShop);
            case "strategy-king":  return StrategyFactory.createPiece('k', x, y, isShop);
            case "barbarian":      return ConflictFactory.createPiece('p', x, y, isShop);
            case "conflict-bishop":return ConflictFactory.createPiece('b', x, y, isShop);
            case "cavalry":        return ConflictFactory.createPiece('h', x, y, isShop);
            case "berserker":      return ConflictFactory.createPiece('q', x, y, isShop);
            case "trebuchet":      return ConflictFactory.createPiece('c', x, y, isShop);
            case "conflict-king":  return ConflictFactory.createPiece('k', x, y, isShop);
            case "loss-pawn":      return LossFactory.createPiece('p', x, y, isShop);
            case "loss-bishop":    return LossFactory.createPiece('b', x, y, isShop);
            case "horseless-headman": return LossFactory.createPiece('h', x, y, isShop);
            case "loss-castle":    return LossFactory.createPiece('c', x, y, isShop);
            case "phylactery-queen": return LossFactory.createPiece('q', x, y, isShop);
            case "loss-king":      return LossFactory.createPiece('k', x, y, isShop);
            default:               return null;
        }
    }
}
