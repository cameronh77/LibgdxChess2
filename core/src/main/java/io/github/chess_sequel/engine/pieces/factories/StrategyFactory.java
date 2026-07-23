package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.*;

/** Creates strategy-faction pieces from a single-character type code. */
public class StrategyFactory {

    public static Piece createPiece(char pieceType, int col, int row, boolean isShop) {
        boolean isBlack = !isShop;
        switch (pieceType) {
            case 'p': return new TrapPawn(col, row, isBlack);
            case 'b': return new ProphetBishop(col, row, isBlack);
            case 'h': return new Commander(col, row, isBlack);
            case 'c': return new Rampart(col, row, isBlack);
            case 'q': return new StrategyQueen(col, row, isBlack);
            case 'k': return new StrategyKing(col, row, isBlack);
            default:  return null;
        }
    }
}
