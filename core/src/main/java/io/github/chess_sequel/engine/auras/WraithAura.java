package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.loss.LossBishop;

import java.util.ArrayList;

/**
 * Board-level aura emitted by the {@link LossBishop}. When the bishop is in its phased state,
 * this aura strips any enemy move that would land on the bishop's tile, making it untargetable
 * on alternating turns.
 */
public class WraithAura extends Aura {

    private final LossBishop bishop;

    public WraithAura(LossBishop bishop) {
        super(bishop, "wraithAura");
        this.bishop = bishop;
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (!bishop.isPhased()) return moves;
        if (piece.isBlack() == bishop.isBlack()) return moves;
        final int bx = bishop.getCol(), by = bishop.getRow();
        moves.removeIf(m -> m.getNewX() == bx && m.getNewY() == by);
        return moves;
    }
}
