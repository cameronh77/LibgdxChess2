package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Castle;
import io.github.chess_sequel.engine.pieces.classic.Pawn;
import io.github.chess_sequel.engine.powers.pieceAltering.Stuck;

public class TollGate extends Castle {



    public TollGate(int x, int y, boolean isWhite){
        super(x, y, isWhite, "toll-gate", ChessClass.GOBLIN);
    }


}
