package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;
import io.github.chess_sequel.engine.pieces.war.strategy.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Central factory that maps string identifiers (from JSON) to concrete piece instances.
 * Delegates to {@link ClassicFactory} or {@link GoblinFactory} based on the faction prefix.
 */
public class PieceFactory {

    /** All registered piece IDs — used by the test screen to enumerate available pieces. */
    public static final List<String> ALL_PIECE_IDS = Collections.unmodifiableList(Arrays.asList(
        "classic-pawn", "classic-bishop", "classic-horse", "classic-queen", "classic-king", "classic-castle",
        "goblin-pawn", "goblin-drill", "slime-steed", "goblin-queen", "goblin-king", "toll-gate",
        "strategy-king", "strategy-queen", "rampart", "trap-pawn", "prophet-bishop", "commander"
    ));

    /**
     * Creates a piece for the given name string, position, and context.
     * @param isShop {@code true} when the piece is being created for a shop display rather than active play
     */
    public static Piece generatePiece(String pieceName, int x, int y, boolean isShop) {
        switch (pieceName) {
            case "classic-pawn":    return ClassicFactory.createPiece('p', x, y, isShop);
            case "classic-bishop":  return ClassicFactory.createPiece('b', x, y, isShop);
            case "classic-horse":   return ClassicFactory.createPiece('h', x, y, isShop);
            case "classic-queen":   return ClassicFactory.createPiece('q', x, y, isShop);
            case "classic-king":    return ClassicFactory.createPiece('k', x, y, isShop);
            case "classic-castle":  return ClassicFactory.createPiece('c', x, y, isShop);
            case "goblin-pawn":     return GoblinFactory.createPiece('p', x, y, isShop);
            case "goblin-drill":    return GoblinFactory.createPiece('b', x, y, isShop);
            case "slime-steed":     return GoblinFactory.createPiece('h', x, y, isShop);
            case "goblin-queen":    return GoblinFactory.createPiece('q', x, y, isShop);
            case "goblin-king":     return GoblinFactory.createPiece('k', x, y, isShop);
            case "toll-gate":       return GoblinFactory.createPiece('c', x, y, isShop);
            case "strategy-king":   return new StrategyKing(x, y, !isShop);
            case "strategy-queen":  return new StrategyQueen(x, y, !isShop);
            case "rampart":         return new Rampart(x, y, !isShop);
            case "trap-pawn":       return new TrapPawn(x, y, !isShop);
            case "prophet-bishop":  return new ProphetBishop(x, y, !isShop);
            case "commander":       return new Commander(x, y, !isShop);
            default:                return null;
        }
    }
}
