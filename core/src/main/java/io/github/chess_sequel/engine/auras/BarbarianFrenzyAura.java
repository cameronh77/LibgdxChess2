package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TurnCondition;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Board-level aura emitted by the Barbarian pawn. While a Barbarian frenzy TurnCondition
 * is active, every friendly piece that has been marked as "already acted" has its move
 * list cleared — effectively locking the extra turn to the Barbarian alone.
 */
public class BarbarianFrenzyAura extends Aura {

    public BarbarianFrenzyAura(Piece owner) {
        super(owner, "barbarianFrenzyAura");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        TurnCondition c = board.getTurnCondition();
        if (c == null || c.frenzySide != owner.isBlack()) return moves;
        if (piece.isBlack() != owner.isBlack()) return moves;
        if (c.hasActed(piece)) {
            moves.clear();
        }
        return moves;
    }
}
