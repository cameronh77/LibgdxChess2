package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.King;

/** The goblin faction's king. Behaves identically to a standard King but uses goblin-themed assets. */
public class GoblinKing extends King {

    public GoblinKing(int x, int y, boolean isBlack){
        super(x, y, isBlack, "goblin-king", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Moves one square in any direction. Defeat the goblin king to win the battle."; }
}
