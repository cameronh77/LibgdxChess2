package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.loss.LossKing;

import java.util.ArrayList;

/**
 * Board-level aura emitted by the {@link io.github.chess_sequel.engine.pieces.war.loss.PhylacteryQueen}.
 * While the queen is alive, enemy pieces cannot generate moves that land on the Loss King's tile,
 * making it effectively uncapturable until the phylactery is destroyed first.
 */
public class PhylacteryAura extends Aura {

    public PhylacteryAura(Piece owner) {
        super(owner, "phylacteryAura");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (piece.isBlack() == owner.isBlack()) return moves;

        LossKing lossKing = null;
        for (Piece p : board.getPieces()) {
            if (p instanceof LossKing && p.isBlack() == owner.isBlack()) {
                lossKing = (LossKing) p;
                break;
            }
        }
        if (lossKing == null) return moves;

        final int kx = lossKing.getCol(), ky = lossKing.getRow();
        moves.removeIf(m -> m.getNewX() == kx && m.getNewY() == ky);
        return moves;
    }
}
