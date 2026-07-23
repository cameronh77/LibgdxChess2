package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.conflict.*;

/**
 * Creates conflict-faction pieces from a single-character type code.
 * Shop pieces are created as white ({@code isShop = true} → {@code isBlack = false}).
 */
public class ConflictFactory {

    /** Maps type char ('p', 'b', 'h', 'q', 'c') to the corresponding conflict piece. */
    public static Piece createPiece(char pieceType, int col, int row, boolean isShop) {
        switch (pieceType) {
            case 'p': return new Barbarian(col, row, !isShop);
            case 'b': return new ConflictBishop(col, row, !isShop);
            case 'h': return new Cavalry(col, row, !isShop);
            case 'q': return new Berserker(col, row, !isShop);
            case 'c': return new Trebuchet(col, row, !isShop);
            case 'k': return new ConflictKing(col, row, !isShop);
            default:  return null;
        }
    }
}
