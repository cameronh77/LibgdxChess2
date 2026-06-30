package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;

/**
 * Creates goblin faction pieces from a single-character type code.
 * Shop pieces are created as white ({@code isBlack = false}) so they render correctly in the shop UI.
 */
public class GoblinFactory {

    /** Maps type char ('p', 'b', 'h', 'q', 'k', 'c') to the corresponding goblin piece. */
    public static Piece createPiece(Character pieceType, int col, int row, boolean isShop){
        Piece piece = null;
        switch (pieceType) {
            case 'p':
                piece = new Goblin(col, row, !isShop);
                break;
            case 'b':
                piece = new GoblinDrill(col, row, !isShop);
                break;
            case 'h':
                piece = new SlimeSteed(col, row, !isShop);
                break;
            case 'q':
                piece = new GoblinQueen(col, row, !isShop);
                break;
            case 'k':
                piece = new GoblinKing(col, row, !isShop);
                break;
            case 'c':
                piece = new TollGate(col, row, !isShop);
                break;
        }
        return piece;
    }
}
