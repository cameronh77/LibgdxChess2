package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.loss.*;

/** Maps single-char identifiers to Loss faction piece instances. */
public class LossFactory {

    public static Piece createPiece(char pieceChar, int x, int y, boolean isShop) {
        boolean isBlack = !isShop;
        switch (pieceChar) {
            case 'p': return new LossPawn(x, y, isBlack);
            case 'b': return new LossBishop(x, y, isBlack);
            case 'h': return new HorselessHeadman(x, y, isBlack);
            case 'c': return new LossCastle(x, y, isBlack);
            case 'q': return new PhylacteryQueen(x, y, isBlack);
            case 'k': return new LossKing(x, y, isBlack);
            default:  return null;
        }
    }
}
