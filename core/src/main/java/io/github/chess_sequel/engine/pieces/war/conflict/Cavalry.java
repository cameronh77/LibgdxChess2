package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.auras.CavalryAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Horse;

/**
 * Conflict knight. Moves like a Horse. Emits a CavalryAura that lets any other friendly
 * piece move onto this tile to absorb it, forming a CombinedPiece with the movement of
 * both a horse and the absorbing piece.
 */
public class Cavalry extends Horse {

    private Board activeBoard;
    private final CavalryAura cavalryAura = new CavalryAura(this);

    public Cavalry(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "cavalry", ChessClass.CONFLICT);
    }

    @Override
    public String getDescription() {
        return "Moves like a horse. A friendly piece can absorb it to gain horse movement on top of its own.";
    }

    @Override
    public void onStart(Board board) {
        activeBoard = board;
        board.addAura(cavalryAura);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) activeBoard.removeAura(cavalryAura);
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) activeBoard.addAura(cavalryAura);
    }
}
