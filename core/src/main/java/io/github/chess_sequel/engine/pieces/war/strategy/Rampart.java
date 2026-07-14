package io.github.chess_sequel.engine.pieces.war.strategy;

import io.github.chess_sequel.engine.auras.TollGateAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Castle;

/**
 * War piece — strategy path.
 *
 * Moves like a standard rook. Emits an aura that prevents any enemy piece from
 * crossing the row the Rampart currently occupies — the aura updates dynamically
 * as the Rampart moves.
 */
public class Rampart extends Castle {

    private Board activeBoard;
    private final TollGateAura rowBlockAura = new TollGateAura(this);

    public Rampart(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "rampart", ChessClass.STRATEGY);
    }

    @Override
    public String getDescription() {
        return "Moves like a rook. Enemies cannot cross the row it occupies.";
    }

    @Override
    public void onStart(Board board) {
        this.activeBoard = board;
        board.addAura(rowBlockAura);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) activeBoard.removeAura(rowBlockAura);
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) activeBoard.addAura(rowBlockAura);
    }
}
