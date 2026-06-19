package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.*;


public class ClassicFactory {

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
