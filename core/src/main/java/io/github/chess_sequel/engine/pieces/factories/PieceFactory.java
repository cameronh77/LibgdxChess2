package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;

public class PieceFactory {

    public static Piece generatePiece(String string, boolean isShop){

        Piece piece = null;

        switch (string.charAt(0)) {
            case 'c':
                piece = ClassicFactory.createPiece(string.charAt(1), Character.getNumericValue(string.charAt(2)), Character.getNumericValue(string.charAt(3)), isShop);
                break;
            case 'g':
                piece = GoblinFactory.createPiece(string.charAt(1), Character.getNumericValue(string.charAt(2)), Character.getNumericValue(string.charAt(3)), isShop);
                break;

        }
        return piece;
    }
}
