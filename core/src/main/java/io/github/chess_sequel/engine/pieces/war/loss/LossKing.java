package io.github.chess_sequel.engine.pieces.war.loss;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.King;

/** Loss faction king — identical movement to the standard king. Powers assigned via the roster. */
public class LossKing extends King {

    public LossKing(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "loss-king", ChessClass.LOSS);
    }

    @Override
    public String getDescription() { return "Moves one square in any direction. Protect it at all costs — if it falls, you lose."; }
}
