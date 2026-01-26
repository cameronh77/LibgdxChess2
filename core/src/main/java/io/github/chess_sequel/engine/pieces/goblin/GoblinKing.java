package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.King;

public class GoblinKing extends King {

    public GoblinKing(int x, int y, boolean isWhite){
        super(x, y, isWhite, "goblin-king", ChessClass.GOBLIN);
    }
}
