package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.*;

public class GoblinFactory {

    public static Piece createPiece(Character pieceType, int col, int row){
        Piece piece = null;
        switch (pieceType) {
            case 'p':
                piece = new Goblin(col, row, true);
                break;
            case 'b':
                piece = new GoblinDrill(col, row, true);
                break;
            case 'h':
                piece = new SlimeSteed(col, row, true);
                break;
            case 'q':
                piece = new GoblinQueen(col, row, true);
                break;
            case 'k':
                piece = new GoblinKing(col, row, true);
                break;
            case 'c':
                piece = new TollGate(col, row, true);
                break;
        }
        return piece;
    }
}
