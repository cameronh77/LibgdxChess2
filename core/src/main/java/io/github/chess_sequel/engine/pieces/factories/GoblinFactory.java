package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;

public class GoblinFactory {

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
