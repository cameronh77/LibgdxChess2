package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.CavalryAbsorbMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.pieces.war.conflict.Cavalry;
import io.github.chess_sequel.engine.pieces.war.conflict.CombinedPiece;

import java.util.ArrayList;

/**
 * Board-level aura emitted by the Cavalry. Offers every friendly non-Cavalry piece a
 * CavalryAbsorbMove targeting the Cavalry's current tile, allowing it to be absorbed
 * and form a CombinedPiece.
 */
public class CavalryAura extends Aura {

    private final Cavalry cavalry;

    public CavalryAura(Cavalry cavalry) {
        super(cavalry, "cavalryAura");
        this.cavalry = cavalry;
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (piece == cavalry) return moves;
        if (piece instanceof CombinedPiece) return moves;
        if (piece.getPieceType() == PieceType.HORSE) return moves;
        if (piece.getPieceType() == PieceType.KING) return moves;
        if (piece.isBlack() != cavalry.isBlack()) return moves;

        CavalryAbsorbMove absorb = new CavalryAbsorbMove(piece, cavalry, board);
        if (!ignoreCheck && board.checkEvaluator(absorb)) return moves;

        moves.add(absorb);
        return moves;
    }
}
