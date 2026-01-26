package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.*;


public class ClassicFactory {

    public static Piece createPiece(Character pieceType, int col, int row){
        Piece piece = null;
        switch (pieceType) {
            case 'p':
                piece = new Pawn(col, row, true);
                break;
            case 'b':
                piece = new Bishop(col, row, true);
                break;
            case 'h':
                piece = new Horse(col, row, true);
                break;
            case 'q':
                piece = new Queen(col, row, true);
                break;
            case 'k':
                piece = new King(col, row, true);
                break;
            case 'c':
                piece = new Castle(col, row, true);
                break;
        }
        return piece;
    }
}
