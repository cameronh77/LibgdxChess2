package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TurnCondition;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Passive goblin king power: after any friendly capture, all friendly pieces that haven't
 * moved yet this frenzy chain may each make one move. Capturing extends the chain; any
 * non-capture move or exhausting all pieces ends it and hands the turn to the opponent.
 */
public class BloodFrenzyPassive extends PassiveKingPower {

    public BloodFrenzyPassive(Piece king) {
        super(king, "Blood Frenzy");
    }

    /** Returns true if Blood Frenzy is active for the given team on this board. */
    public static boolean isActive(Board board, boolean isBlack) {
        for (Aura a : board.getBoardAuras()) {
            if (a instanceof BloodFrenzyPassive && ((BloodFrenzyPassive) a).owner.isBlack() == isBlack) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        TurnCondition c = board.getTurnCondition();
        if (c == null || c.frenzySide != owner.isBlack()) return moves;
        if (piece.isBlack() != owner.isBlack()) return moves;
        if (c.hasActed(piece)) {
            moves.clear();
            return moves;
        }
        return moves;
    }

    @Override
    public String getIconPath() { return "kingPowers/blood-frenzy.png"; }

    @Override
    public String getDescription() {
        return "After any friendly capture, all other friendly pieces may each make one move. Capturing continues the chain; any other move ends it.";
    }
}
