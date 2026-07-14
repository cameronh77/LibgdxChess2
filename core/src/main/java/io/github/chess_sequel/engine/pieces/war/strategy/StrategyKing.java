package io.github.chess_sequel.engine.pieces.war.strategy;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.King;

/** The strategy faction's king. Behaves identically to a standard King but uses strategy-themed assets. */
public class StrategyKing extends King {

    public StrategyKing(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "strategy-king", ChessClass.STRATEGY);
    }

    @Override
    public String getDescription() { return "Moves one square in any direction. Protect it at all costs — if it falls, you lose."; }
}
