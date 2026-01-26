package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Horse;

public class SlimeSteed extends Horse {
    public SlimeSteed(int x, int y, boolean isWhite){
        super(x, y, isWhite, "slime-steed", ChessClass.GOBLIN);
    }
}
