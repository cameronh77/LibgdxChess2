package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Pawn;
import io.github.chess_sequel.engine.powers.pieceAltering.Stuck;

public class Goblin extends Pawn {

    private Stuck stuck = new Stuck(3);
    public Goblin(int x, int y, boolean isWhite){
        super(x, y, isWhite, "goblin", ChessClass.GOBLIN);
    }

    @Override
    public void onCapture(Piece piece){
        piece.getAlterMovePowers().add(stuck);
    }

    @Override
    public void undoOnCapture(Piece piece){
        piece.getAlterMovePowers().remove(stuck);
    }
}
