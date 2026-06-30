package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.*;


/**
 * Creates classic chess pieces from a single-character type code.
 * Shop pieces are created as white ({@code isBlack = false}) so they render correctly in the shop UI.
 */
public class ClassicFactory {

    /** Maps type char ('p', 'b', 'h', 'q', 'k', 'c') to the corresponding classic piece. */
    public static Piece createPiece(Character pieceType, int col, int row, boolean isShop){
        Piece piece = null;
        switch (pieceType) {
            case 'p':
                piece = new Pawn(col, row, !isShop);
                break;
            case 'b':
                piece = new Bishop(col, row, !isShop);
                break;
            case 'h':
                piece = new Horse(col, row, !isShop);
                break;
            case 'q':
                piece = new Queen(col, row, !isShop);
                break;
            case 'k':
                piece = new King(col, row, !isShop);
                break;
            case 'c':
                piece = new Castle(col, row, !isShop);
                break;
        }
        return piece;
    }
}
