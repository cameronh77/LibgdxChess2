package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.auras.TollGateAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Castle;

public class TollGate extends Castle {

    private Board activeBoard;
    private final TollGateAura aura = new TollGateAura(this);

    public TollGate(int x, int y, boolean isBlack){
        super(x, y, isBlack, "toll-gate", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Moves horizontally or vertically. Emits an aura that blocks all enemy movement through adjacent tiles."; }

    @Override
    public void onStart(Board board){
        this.activeBoard = board;
        board.addAura(aura);
    }

    @Override
    public void onCapture(Piece piece){
        if (activeBoard != null) activeBoard.removeAura(aura);
    }

    @Override
    public void undoOnCapture(Piece piece){
        if (activeBoard != null) activeBoard.addAura(aura);
    }
}
