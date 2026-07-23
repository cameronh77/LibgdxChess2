package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.King;

/** The conflict faction's king. Behaves identically to a standard King but uses conflict-themed assets. */
public class ConflictKing extends King {

    public ConflictKing(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "conflict-king", ChessClass.CONFLICT);
    }

    @Override
    public String getDescription() { return "Moves one square in any direction. Protect it at all costs — if it falls, you lose."; }
}
